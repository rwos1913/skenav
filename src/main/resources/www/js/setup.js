window.onload = async function requestdefdirectory() {
	listenForSubmit();
	console.log("test");
}

function listenForSubmit() {
	document.getElementById("setupform").addEventListener("submit", preventRedirect);
}

function preventRedirect(event) {
	var request = new XMLHttpRequest();
	request.open("POST", "/setup/submitowner", true);
	request.onload = () => {
		console.log("admin form sucessfully submitted");
	}
	request.onerror = function() {
		alert("failed to submit user information");
	}
	request.send(new FormData(event.target));
	event.preventDefault();
}
