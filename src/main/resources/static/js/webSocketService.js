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
            stompClient.publish({
                destination: "/pub/auth.refresh",
                headers: {'Authorization': TokenManager.getAccessToken(), 'receipt': receiptId},
                body: JSON.stringify({accessToken: TokenManager.getAccessToken()})
            });

            return new Promise((resolve) => { // receiptId로 서버로 부터 응답받은 메시지 열어보는 방법? 자꾸 여기서 한 텀 쉬는 문제
                stompClient.watchForReceipt(receiptId, () => {
                    console.log('💡 [Token refresh confirmed]');
                    resolve(true);
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
    stompClient.subscribe('/sub/chat.room.1', onMessageReceived); // Message Subscription
    stompClient.subscribe('/user/queue/errors', onErrorReceived); // Error Handling
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