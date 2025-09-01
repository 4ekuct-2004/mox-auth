document.getElementById('registerForm').addEventListener('submit', async function(event) {
    event.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    document.querySelectorAll('.error').forEach(el => el.textContent = '');
    document.getElementById('error-message').style.display = 'none';
    document.getElementById('success-message').style.display = 'none';

    let isValid = true;

    if (username.length < 3 || username.length > 20) {
        document.getElementById('username-error').textContent = 'Логин должен содержать от 3 до 20 символов';
        isValid = false;
    }

    if (password.length < 6) {
        document.getElementById('password-error').textContent = 'Пароль должен содержать минимум 6 символов';
        isValid = false;
    }

    if (password !== confirmPassword) {
        document.getElementById('confirm-password-error').textContent = 'Пароли не совпадают';
        isValid = false;
    }

    if (!isValid) {
        return;
    }

    try {
        const response = await fetch('/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': getCsrfToken()
            },
            body: JSON.stringify({ username, password })
        });

        if (response.ok) {
            document.getElementById('success-message').textContent = 'Регистрация прошла успешно!';
            document.getElementById('success-message').style.display = 'block';

            document.getElementById('registerForm').reset();

            setTimeout(() => {
                window.location.href = '/login';
            }, 2000);
        } else {
            const errorText = await response.text();
            document.getElementById('error-message').textContent = errorText;
            document.getElementById('error-message').style.display = 'block';
        }
    } catch (error) {
        document.getElementById('error-message').textContent = 'Ошибка сети: ' + error.message;
        document.getElementById('error-message').style.display = 'block';
    }
});

function getCsrfToken() {
    const csrfTokenInput = document.querySelector('input[name="_csrf"]');
    return csrfTokenInput ? csrfTokenInput.value : '';
}