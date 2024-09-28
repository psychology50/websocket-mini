export const showMessage = (message) => {
    const messageElement = document.createElement('div');
    messageElement.appendChild(document.createTextNode(message));
    document.getElementById('messages').appendChild(messageElement);
}

export function showLoginSection() {
    document.getElementById('loginSection').style.display = 'block';
    document.getElementById('chatSection').style.display = 'none';
    document.getElementById('logoutButton').style.display = 'none';
}

export function showChatSection() {
    document.getElementById('loginSection').style.display = 'none';
    document.getElementById('chatSection').style.display = 'block';
    document.getElementById('logoutButton').style.display = 'block';
}

export function setLoginError(message) {
    document.getElementById('loginError').textContent = message;
}

export function clearLoginError() {
    document.getElementById('loginError').textContent = '';
}