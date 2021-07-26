function upload() {
    var fileupload = document.getElementById("file");
    fileupload.click();
}
function uploadprompt() {
    var fileupload = document.getElementById("file").files[0];
    var req = new XMLHttpRequest();
    var formData= new FormData();

    formData.append("file", fileupload);
    req.open("POST","/upload");
    req.send(formData);
}
function viewfiles() {

}