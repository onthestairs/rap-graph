//alert('hi');


$(function() {

    $('#fromArtist').autocomplete({
        serviceUrl: '/artist-autocomplete',
        // onSelect: function (suggestion) {
        //     alert('You selected: ' + suggestion.value + ', ' + suggestion.data);
        // }
    });

    $('#toArtist').autocomplete({
        serviceUrl: '/artist-autocomplete',
        // onSelect: function (suggestion) {
        //     alert('You selected: ' + suggestion.value + ', ' + suggestion.data);
        // }
    });
    
    $('.pathForm').submit(function(e) {
        e.preventDefault();
        $('#pathSubmit').val('Loading...');
        var fromArtist = $('#fromArtist').val();
        var toArtist = $('#toArtist').val();
        var data = {
            'from-artist': fromArtist,
            'to-artist': toArtist
        };
        $.get("path", data, function(path) {
            $path = $('.path');
            $path.empty();
            for(var i=0; i<path.length; i++) {
                var step = path[i];
                $path.append('<div class="artist"><div class="artistImage" style="background-image: url('+step['from-artist-image']+');"></div><div class="artistName">'+step['from-artist']+'</div></div>');
                $path.append('<div class="song"><div class="topArrow arrow"></div><div class="songLink"><a href="'+step.song.url+'">'+step.song.title+'</a></div><div class="bottomArrow arrow"></div></div>');
                if(i == (path.length-1)) {
                    $path.append('<div class="artist"><div class="artistImage" style="background-image: url('+step['to-artist-image']+');"></div><div class="artistName">'+step['to-artist']+'</div></div>');
                }
            }
            $('#pathSubmit').val('Go');
        });
    });
});
