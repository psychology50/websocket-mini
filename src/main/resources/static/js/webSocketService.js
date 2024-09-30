import { Client } from '@stomp/stompjs';
import { CONFIG } from './config.js';
import { TokenManager } from "./tokenManager.js";
import { getSocketServerUrl } from "./apiService.js";

let stompClient = null;

// WebSocket Connection
export const connectWebSocket = async () => {
    try {
        const url = await getSocketServerUrl();
        console.log("🟢 Connecting to:", url);

        stompClient = new Client({
            brokerURL: url,
            connectHeaders: {
                Authorization: TokenManager.getAccessToken()
            },
            onConnect: onConnected,
            onStompError: onError,
            onWebSocketError: onError
        });

        stompClient.activate();
        return true;
    } catch (error) {
        console.error('WebSocket connection error:', error);
        return false;
    }
}

export const sendMessage = (roomId, content) => {
    stompClient.publish({
        destination: "/pub/chat.message." + roomId,
        headers: {'Authorization': TokenManager.getAccessToken()},
        body: JSON.stringify({
            'roomId': roomId,
            'content': content
        })
    });
    console.log('📤 [Send Message]: ', content);
}

export const refresh = async () => {
    try {
        const response = await fetch(`${CONFIG.API_URL}/api/auth/refresh`, {
            method: 'GET',
            credentials: 'include'
        });

        if (response.ok) {
            TokenManager.setAccessToken(response.headers.get('Authorization'));

            const receiptId = 'receipt-' + Date.now();

            return new Promise((resolve, reject) => {
                stompClient.watchForReceipt(receiptId, (frame) => { // publish 하기 전에 watchForReceipt로 응답을 기다려야 함
                    console.log('🔄 [Token refresh confirmed] frame:', frame);
                    console.log('🟢 [Token refresh confirmed] headers:', frame.headers);
                    console.log('🟢 [Token refresh confirmed] raw body:', frame.body);

                    // frame.body 뒤에 " MESSAGE\n" 문자열이 붙어 있어서 JSON.parse() 시 에러 발생
                    // 따라서, frame.body를 JSON.parse() 하기 전에 문자열 뒤의 " MESSAGE\n" 문자열을 제거
                    // try {
                    //     const serverMessage = JSON.parse(frame.body.replace(' MESSAGE\n', ''));
                    //     if (serverMessage.code === '2000') { // 성공 코드 확인
                    //         resolve(true); // 성공 시
                    //     } else {
                    //         resolve(false); // 실패 시
                    //     }
                    // } catch (error) {
                    //     console.error('🔴 Failed to parse receipt body:', error);
                    //     resolve(false); // 실패 시
                    // }

                    try {
                        // JSON 부분만 추출 (body 출력해보면 json 뒤에 온갖 메타데이터가 붙어있음)
                        const jsonMatch = frame.body.match(/\{.*\}/);
                        if (jsonMatch) {
                            const jsonStr = jsonMatch[0];
                            console.log('🟢 [Token refresh confirmed] Extracted JSON:', jsonStr);
                            const serverMessage = JSON.parse(jsonStr);
                            console.log('🟢 [Token refresh confirmed] Parsed message:', serverMessage);

                            if (serverMessage.code === '2000') {
                                resolve(true);
                            } else {
                                resolve(false);
                            }
                        } else {
                            console.error('No JSON found in the frame body');
                            resolve(false);
                        }
                    } catch (error) {
                        console.error('🔴 Failed to parse receipt body:', error);
                        resolve(false);
                    }
                });

                stompClient.publish({
                    destination: "/pub/auth.refresh",
                    headers: {'Authorization': TokenManager.getAccessToken(), 'receipt': receiptId},
                    body: JSON.stringify({accessToken: TokenManager.getAccessToken()})
                });
            });
        }
        return true;
    } catch (error) {
        console.error('🔴 [webSocketService.refresh()] Token refresh failed:', error);
        return false
    }
}

const onConnected = (frame) => {
    console.log('Connected: ' + frame);

    let chatRoomReceiptId = 'chat-room-receipt-' + Date.now();
    let errorReceiptId = 'error-receipt-' + Date.now();

    stompClient.watchForReceipt(chatRoomReceiptId, (frame) => {
        console.log('🟢 [Chat Room Subscription] frame:', frame);
        console.log('🟢 [Chat Room Subscription] headers:', frame.headers);
        console.log('🟢 [Chat Room Subscription] raw body:', frame.body);
    });
    stompClient.watchForReceipt(errorReceiptId, (frame) => {
        console.log('🟢 [Error Subscription] frame:', frame);
        console.log('🟢 [Error Subscription] headers:', frame.headers);
        console.log('🟢 [Error Subscription] raw body:', frame.body);
    });

    stompClient.subscribe('/sub/chat.room.1', onMessageReceived, {'receipt': chatRoomReceiptId}); // Message Subscription
    stompClient.subscribe('/user/queue/errors', onErrorReceived, {'receipt': errorReceiptId}); // Error Handling
}

const onError = (error) => {
    console.error('WebSocket error:', error);
    if (error.headers && error.headers.message.startsWith('401')) {
        refresh().then(success => {
            if (success) { // 성공하면, 기존에 실패했던 요청 재시도 로직

            } else { // UI 업데이트는 main.js에서 처리
                window.dispatchEvent(new CustomEvent('webSocketAuthFailed'));
            }
        });
    }
}

function onMessageReceived(message) {
    console.log('📩 [webSocketService.onMessageReceived] Received:', message);
    // UI 업데이트는 main.js에서 처리
    window.dispatchEvent(new CustomEvent('newMessage', { detail: JSON.parse(message.body) }));
}

const onErrorReceived = (message) => {
    const error = JSON.parse(message.body);
    console.log('🔴 Received error:', error);

    // header message가 401로 시작하면
    if (error['code'] === '4011') {
        refresh().then(success => {
            if (!success) {
                // UI 업데이트는 main.js에서 처리
                window.dispatchEvent(new CustomEvent('webSocketAuthFailed'));
            } else {
                // 재시도 로직
                console.log('[🟢 Refresh 성공] 기존 요청 재시도')
            }
        });
    }
}