const copyButton = document.querySelector('#copy-button')
const copyTextBlock = document.querySelector('#copy-text')
const successText = document.querySelector('.success-text')

const copyFunc = () => {
    const textToCopy = copyTextBlock.innerText;

    navigator.clipboard.writeText(textToCopy)
        .then(() => {

            successText.classList.add('show');

            setTimeout(() => {
                successText.classList.remove('show');
            }, 1500);
        })
        .catch(err => {
            alert('Не удалось скопировать текст: ' + textToCopy);
            console.error('Не удалось скопировать текст: ', err);
        });
}

copyButton.addEventListener('click', copyFunc)