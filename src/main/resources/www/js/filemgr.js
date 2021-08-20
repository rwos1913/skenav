// TODO: refresh table after upload to show new file
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
function getSearchString() {
    clearTable("tablebody");
    searchvalue = document.getElementById("search")
    var search = searchvalue.value;
    console.log(search)
    let url = "/query?limit=100&search=" + search;
    getJson(url, callback)

}
getJson("/query?limit=100", callback);

function callback (err, data) {
    if (err !== null) {
        alert("something went wrong " + err);
    }else {
        parseJson(data);
    }
}
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
    var td0 = tr.insertCell(0);
    td0.textContent = filename;
    var td1 = tr.insertCell(1);
    td1.textContent = filetype;
    var td2 = tr.insertCell(2);
    td2.textContent = uploaddate;
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
function clearTable (elementID) {
    var div = document.getElementById(elementID);

    while(div.firstChild) {
        div.removeChild(div.firstChild);
    }
}