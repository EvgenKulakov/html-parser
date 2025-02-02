const copyButton = document.querySelector('#copy-button')
const firstTextBlock = document.querySelector('#first-text')
const secondTextBlocks = document.querySelectorAll('.second-text')
const successText = document.querySelector('.success-text')

const copyFunc = () => {
    let textToCopy = firstTextBlock.innerText

    if (secondTextBlocks.length > 0) {
        textToCopy += '\n\n'
        const blocksArray = Array.from(secondTextBlocks);
        textToCopy += blocksArray.map(el => el.innerText).join('\n');
    }

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