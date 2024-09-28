// Token Management
export const TokenManager = {
    setAccessToken: (token) => sessionStorage.setItem('accessToken', "Bearer " + token),
    getAccessToken: () => sessionStorage.getItem('accessToken'),
    clearTokens: () => {
        sessionStorage.removeItem('accessToken');
        document.cookie = 'refreshToken=; Max-Age=0; path=/; secure; samesite=none';
    }
};