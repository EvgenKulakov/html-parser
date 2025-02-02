const copyButton = document.querySelector('#copy-button')
const firstTextBlock = document.querySelector('#first-text')
const secondTextBlocks = document.querySelectorAll('.second-text')
const successText = document.querySelector('.success-text')

const copyFunc = () => {
    let textToCopy = firstTextBlock.innerText + '\n\n'

    secondTextBlocks.forEach(el => {
        textToCopy += el.innerText + '\n'
    })

    navigator.clipboard.writeText(textToCopy)
        .then(() => {

            successText.classList.add('show');

            setTimeout(() => {
                successText.classList.remove('show');
            }, 2000);
        })
        .catch(err => {
            alert('Не удалось скопировать текст: ' + textToCopy);
            console.error('Не удалось скопировать текст: ', err);
        });
}

copyButton.addEventListener('click', copyFunc)