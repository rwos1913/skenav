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
let search = "";
let sort = "";
function getSearchString() {
    //clearTable("tablebody");
    searchvalue = document.getElementById("search");
    search = searchvalue.value;
    console.log(search);
    let url = "/query?limit=100&search=" + search + "&sort=" + sort;
    getJson(url, callback);

}
function fileNameSort() {
    if (sort == 2) {
        sort = 3;
    }
    else {
        sort = 2;
    }
    console.log(sort);
    let url = "/query?limit=100&search=" + search + "&sort=" + sort;
    console.log(url);
    getJson(url, callback);
}
function uploadDateSort() {
    if (sort == 0) {
        sort = 1;
    }
    else {
        sort = 0;
    }
    let url = "/query?limit=100&search=" + search + "&sort=" + sort;
    getJson(url, callback);
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
    clearTable();
    for (let i = 0; i < data.length; i++) {
        var currentrow = data[i];
        var filename = currentrow[0];
        var filetype = currentrow[1];
        var uploaddate = currentrow[2];
        displayFilesAsTable (filename,filetype,uploaddate, i);

    }
}

function displayFilesAsTable (filename, filetype, uploaddate, i) {
    var table = document.getElementById("tablebody");
    var tr = document.createElement("tr");
    if ((i + 1)%2 == 0) {
        tr.className = "eventablerow";
    }
    else {
        tr.className = "oddtablerow";
    }
    tr.onclick = function() {requestVideo(filename)};
    var td0 = tr.insertCell(0);
    td0.textContent = filename;
    var td1 = tr.insertCell(1);
    td1.textContent = filetype;
    td1.className = "tablecell"
    //TODO: replace file types with icons
    var td2 = tr.insertCell(2);
    td2.className = "tablecell"
    td2.textContent = uploaddate;
    table.appendChild(tr);
}
function requestVideo(filename) {
    console.log(filename);
    var videoUrl = "/video?name=" + filename;
    var xhr = new XMLHttpRequest();
    xhr.responseType = "json";
    xhr.onload = () => {
        let data = xhr.response;
        playVideo(data);
    }
    xhr.open( "GET", videoUrl, true);
    xhr.send(null);
}
function playVideo(playlistname) {
    var videodiv = document.createElement("div");
    var video = document.createElement("video")
    videodiv.id = "videoplayer";
    video.id = "video";
    video.controls = true;
    video.autoplay = true;
    videodiv.appendChild(video);
    if (Hls.isSupported()) {
        console.log('hello hls.js');
        //var video = document.getElementById('video');
        var hls = new Hls();
        // bind them together
        hls.attachMedia(video);
        hls.on(Hls.Events.MEDIA_ATTACHED, function () {
            console.log('video and hls.js are now bound together');
            hls.loadSource('/files/hlstestfolder/' + playlistname);
            hls.on(Hls.Events.MANIFEST_PARSED, function (event, data) {
                hls.startLoad(0)
                console.log(
                    'manifest loaded, found' + data.levels.length + 'quality level'
                );
            });
        });
    }
    clearTable();
    document.getElementById("filedisplaybox").appendChild(videodiv);
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

function clearTable () {
    var div = document.getElementById("tablebody");

    while(div.firstChild) {
        div.removeChild(div.firstChild);
    }
}