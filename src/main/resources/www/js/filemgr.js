var getJson = function (url, callback) {
    var xhr = new XMLHttpRequest();
    xhr.open("GET", url, true);
    xhr.responseType = "json";
    xhr.onload = function() {
        var status = xhr.status;
        if (status === 200) {
            callback(null, xhr.response);
        }else{
            callback(status, xhr.response);
        }
    };
    xhr.send();
};
getJson("/query?limit=100",
    function (err, data) {
    if (err !== null) {
        alert("something went wrong " + err);
    }else {
        alert( "Your query count: " + data.query.count);
    }
    });

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