function fazerLogout() {
    localStorage.removeItem('token');
    alert('Você foi desconectado.');
    window.location.href = 'inicio.html';
}