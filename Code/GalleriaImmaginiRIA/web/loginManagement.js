/**
 * Login management
 */

(function() { // avoid variables ending up in the global scope

  const signUpButton = document.getElementById('signUp');
  const signInButton = document.getElementById('signIn');
  const container = document.getElementById('container');

  signUpButton.addEventListener('click', () => {
    container.classList.add("right-panel-active");
  });

  signInButton.addEventListener('click', () => {
    container.classList.remove("right-panel-active");
  });

  var password_signup = document.getElementById("password-signup")
  var confirm_password = document.getElementById("confirm_password");
  var email_signup = document.getElementById("email-signup");

  function validatePassword(){
    if(password_signup.value !== confirm_password.value) {
      confirm_password.setCustomValidity("Passwords Don't Match");
      confirm_password.reportValidity();
    } else {
      confirm_password.setCustomValidity('');
    }
  }

  password_signup.onchange = validatePassword;
  confirm_password.onkeyup = validatePassword;

  const email_regex = new RegExp(
      '^(([^<>()[\\]\\\\.,;:\\s@\\"]+(\\.[^<>()[\\]\\\\.,;:\\s@\\"]+)*)|' +
      '(\\".+\\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])' +
      '|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$'
  );

  function validateEmail() {
    if(email_regex.test(String(email_signup.value).toLowerCase())) {
      email_signup.setCustomValidity('');
    } else {
      email_signup.setCustomValidity("Please enter a valid email");
      email_signup.reportValidity();
    }
  }

  function restoreValidity() {
    email_signup.setCustomValidity('');
  }

  email_signup.onkeyup = restoreValidity; // restore validity while writing
  email_signup.onchange = validateEmail;

  document.getElementById("login-button").addEventListener('click', (e) => {
    var form = e.target.closest("form");
    if (form.checkValidity()) {
      makeCall("POST", 'CheckLogin', e.target.closest("form"),
        function(req) {
          if (req.readyState === XMLHttpRequest.DONE) {
            var message = req.responseText;
            switch (req.status) {
              case 200:
            	sessionStorage.setItem('username', message);
                window.location.href = "Home.html";
                break;
              case 400: // bad request
                document.getElementById("errormessage-login").textContent = message;
                break;
              case 401: // unauthorized
                  document.getElementById("errormessage-login").textContent = message;
                  break;
              case 500: // server error
            	document.getElementById("errormessage-login").textContent = message;
                break;
            }
          }
        }
      );
    } else {
    	 form.reportValidity();
    }
  });

  document.getElementById("signup-button").addEventListener('click', (e) => {
    var form = e.target.closest("form");
    if (form.checkValidity()) {
      makeCall("POST", 'Signup', e.target.closest("form"),
          function(req) {
            if (req.readyState === XMLHttpRequest.DONE) {
              var message = req.responseText;
              switch (req.status) {
                case 200:
                  sessionStorage.setItem('username', message);
                  window.location.href = "Home.html";
                  break;
                case 400: // bad request
                  document.getElementById("errormessage-signup").textContent = message;
                  break;
                case 401: // unauthorized
                  document.getElementById("errormessage-signup").textContent = message;
                  break;
                case 500: // server error
                  document.getElementById("errormessage-signup").textContent = message;
                  break;
              }
            }
          }
      );
    } else {
      form.reportValidity();
    }
  });


})();