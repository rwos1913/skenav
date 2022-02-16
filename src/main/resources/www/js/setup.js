window.onload = async function requestdefdirectory() {
	listenForSubmit();
	console.log("test");
	let response = await fetch("/setup/defuploaddir");
	let defaultuploaddirectory = await response.text();
	let textbox = document.getElementById("uploaddirectory");
	textbox.value = defaultuploaddirectory;
}

function listenForSubmit() {
	document.getElementById("setupform").addEventListener("submit", preventRedirect);
}

function preventRedirect(event) {
	var request = new XMLHttpRequest();
	request.open("POST", "/setup/submitowner", true);
	request.onload = function() {
		console.log("admin form sucessfully submitted");
	}
	request.onerror = function() {
		alert("failed to submit user information");
	}
	request.send(new FormData(event.target));
	event.preventDefault();
}
