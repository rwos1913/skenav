var registerform;
var loginform;
var loginbox;

function setup() {
	registerform = document.getElementById("registerform");
	loginform = document.getElementById("loginform");
	loginbox = document.getElementById("loginbox");
	registerform.remove();
	document.getElementById("loginform").addEventListener("submit", submitForm);
}

function switchToRegister() {
	loginform.remove();
	loginbox.appendChild(registerform);
	document.getElementById("registerform").addEventListener("submit", submitForm);
}

function switchToLogin() {
	registerform.remove();
	loginbox.appendChild(loginform);
	document.getElementById("loginform").addEventListener("submit", submitForm);
}

function submitForm(event) {
	console.log("form submit method triggered");
	if (event.target.id === "loginform") {
		var url = "/login/submitlogin"
		var redirect = true
	}
	else if (event.target.id === "registerform") {
		var url = "/register"
		var redirect = false
	}
	var request = new XMLHttpRequest();
	request.open('POST', url, true);
	request.onload = function () {
		// request successful
		if (redirect === true) {
			window.location.href = "/"
		}
		else {
			switchToLogin();
		}
	};

	request.onerror = function () {
		// request failed
		alert("incorrect password or something")
	}

	request.send(new FormData(event.target));
	event.preventDefault();


}