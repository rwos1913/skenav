window.onload = async function useSubmitListener() {
	listenForSubmit();
	console.log("test");
}

function listenForSubmit() {
	document.getElementById("inviteuserform").addEventListener("submit", preventRedirect);
}

function preventRedirect(event) {
	var request = new XMLHttpRequest();
	request.open("POST", "/settings/generateinvite", true);
	request.onload = function() {
		console.log("admin form sucessfully submitted");
		let data = JSON.parse(request.response);
		showInviteCode(data);
	}
	request.onerror = function() {
		alert("failed to submit user information");
	}
	request.send(new FormData(event.target));
	event.preventDefault();
}

function showInviteCode(data) {
	alert ("invite code is: " + data.invitecode);

}
