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
         parseJson(data);
    }
    });
function parseJson (data) {
    for (let i = 0; i < data.length; i++) {
        var currentrow = data[i];
        var filename = currentrow[0];
        var filetype = currentrow[1];
        var uploaddate = currentrow[2];
        displayFilesAsTable (filename,filetype,uploaddate);

    }
}
function displayFilesAsTable (filename, filetype, uploaddate) {
    var table = document.getElementById("tablebody");
    var tr = document.createElement("tr");
    tr.innerHTML = "<td>" + filename + "</td>" +
        "<td>" + filetype + "</td>" +
        "<td>" + uploaddate + "</td>";
    table.appendChild(tr);
}
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