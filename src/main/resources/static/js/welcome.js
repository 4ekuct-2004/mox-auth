document.addEventListener('DOMContentLoaded', function() {
    const messageForm = document.getElementById('messageForm');
    const messageResponse = document.getElementById('message-response');

    messageForm.addEventListener('submit', async function(event) {
        event.preventDefault();

        const messageInput = document.getElementById('message');
        const message = messageInput.value.trim();
        const csrfToken = document.querySelector('input[name="_csrf"]').value;

        if (!message) {
            showResponse('Пожалуйста, введите сообщение', 'error');
            return;
        }

        try {
            const response = await fetch('/api/message', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': csrfToken
                },
                body: JSON.stringify({ message: message })
            });

            if (response.ok) {
                const responseText = await response.text();
                showResponse(responseText, 'success');
                messageInput.value = '';
            } else {
                showResponse('Ошибка при отправке сообщения', 'error');
            }
        } catch (error) {
            showResponse('Ошибка сети: ' + error.message, 'error');
        }
    });

    function showResponse(text, type) {
        messageResponse.textContent = text;
        messageResponse.className = type === 'success' ?
            'response-message success' : 'response-message error';
        messageResponse.style.display = 'block';

        setTimeout(() => {
            messageResponse.style.display = 'none';
        }, 3000);
    }
});