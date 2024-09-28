import { login, logout } from './apiService.js';
import { connectWebSocket, sendMessage } from './webSocketService.js';
import { showLoginSection, showChatSection, showMessage, setLoginError, clearLoginError } from './uiManager.js';

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('loginButton').addEventListener('click', handleLogin);
    document.getElementById('sendButton').addEventListener('click', handleSendMessage);
    document.getElementById('logoutButton').addEventListener('click', handleLogout);

    window.addEventListener('newMessage', (event) => showMessage(event.detail.content));
    window.addEventListener('webSocketAuthFailed', () => {
        showLoginSection();
        setLoginError('Authentication failed. Please login again.');
    });
});

const handleLogin = async () => {
    const userId = document.getElementById('userId').value;
    clearLoginError();
    if (await login(userId)) {
        if (await connectWebSocket()) {
            showChatSection();
        } else {
            setLoginError('Failed to connect to chat server.');
        }
    } else {
        setLoginError('Login failed. Please try again.');
    }
}

const handleSendMessage = () => {
    const roomId = document.getElementById('roomId').value;
    const content = document.getElementById('message').value;
    sendMessage(roomId, content);
    document.getElementById('message').value = ''; // Clear message input
}

const handleLogout = async () => {
    if (await logout()) {
        showLoginSection();
    } else {
        alert('Logout failed. Please try again.');
    }
}