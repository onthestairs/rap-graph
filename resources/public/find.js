//alert('hi');

 var getArtistImage = function(artistName, callback) {
     var data = {
         'artist': artistName
     }
     $.get("image", data, function(result){
         callback(result);
     });
}

$(function() {
    
    $('.pathForm').submit(function(e) {
        console.log('eaaaaasy');
        e.preventDefault();
        console.log('yo');
        var fromArtist = $('#fromArtist').val();
        var toArtist = $('#toArtist').val();
        var data = {
            'from-artist': fromArtist,
            'to-artist': toArtist
        };
        $.get("path", data, function(path) {
            console.log(path);
        });
    });
});
