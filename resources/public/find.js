//alert('hi');


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
            $path = $('.path');
            $path.empty();
            for(var i=0; i<path.length; i++) {
                var step = path[i];
                $path.append('<div class="artist"><div class="artistImage" style="background-image: url('+step['from-artist-image']+');"></div><div class="artistName">'+step['from-artist']+'</div></div>');
                $path.append('<div class="song"><div class="topArrow arrow"></div><div class="songLink"><a href="#">'+step.song.title+'</a></div><div class="bottomArrow arrow"></div></div>');
                if(i == (path.length-1)) {
                    $path.append('<div class="artist"><div class="artistImage" style="background-image: url('+step['to-artist-image']+');"></div><div class="artistName">'+step['to-artist']+'</div></div>');
                }
            }
        });
    });
});
