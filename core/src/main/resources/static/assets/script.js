// Tooltips
$(function () {
    $('[data-toggle="tooltip"]').tooltip({boundary: 'window'})
})

$(function () {
    'use strict'
    const forms = document.querySelectorAll('.requires-validation')
    Array.from(forms)
        .forEach(function (form) {
            form.addEventListener('submit', function (event) {
                if (!form.checkValidity()) {
                    event.preventDefault()
                    event.stopPropagation()
                }

                form.classList.add('was-validated')
            }, false)
        })
})

//validation
$(function () {
    var input = document.getElementById("phone");

    input.addEventListener("keydown", function (event) {
        // Allow only backspace, delete, tab, numbers, and plus sign
        if (event.key !== 'Backspace' && event.key !== 'Delete' && event.key !== 'Tab' && event.key !== '+' && (event.key < '0' || event.key > '9')) {
            event.preventDefault();
        }
    });

    var emailInput = document.getElementById("email");

    emailError = document.querySelector('#email + .help-block');
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    emailInput.addEventListener('input', function () {
        if (emailInput.value.length==0 || emailRegex.test(emailInput.value)) {
            emailError.textContent = '';
        } else {
            emailError.textContent = 'Zadejte platný email';
        }
    });


    var emailAuthInput = document.getElementById("email-author");

    authorEmailError = document.querySelector('#email-author + .help-block');

    emailAuthInput.addEventListener('input', function () {
        if (emailAuthInput.value.length==0 || emailRegex.test(emailAuthInput.value)) {
            authorEmailError.textContent = '';
        } else {
            authorEmailError.textContent = 'Zadejte platný email';
        }
    });

    //www listener
    const urlInput = document.getElementById('website');
    const urlError = document.querySelector('#website + .help-block');
    const urlRegex = /^(http:\/\/www\.|https:\/\/www\.|http:\/\/|https:\/\/)?[a-zA-Z0-9]+([-.][a-zA-Z0-9]+)*\.[a-zA-Z]{2,}(\/.*)?$/;

    urlInput.addEventListener('input', function() {
        if (urlInput.value.length==0 || urlRegex.test(urlInput.value)) {
            urlError.textContent = '';
        } else {
            urlError.textContent = 'Zadejte platnou URL';
        }
    });

})

//Shows fields
function toggleEmailInputBox() {
    var inputBox = document.getElementById("email");
    var hiddenField = document.getElementById("emailHiddenFields");

    if (inputBox.value) {
        hiddenField.classList.add("show");
    } else {
        hiddenField.classList.remove("show");
    }
}

function togglePhoneInputBox() {
    var inputBox = document.getElementById("phone");
    var hiddenField = document.getElementById("phoneHiddenFields");

    if (inputBox.value) {
        hiddenField.classList.add("show");
    } else {
        hiddenField.classList.remove("show");
    }
}

