$(document).ready(function() {
  init();
});

var filter = "all";

function init() {
    // On filter, refresh data.
    $( 'a[data-toggle="pill"]' ).on( 'shown.bs.tab', function( evt ) {       
        filter = $(evt.target).attr('id');
        search();
    });
}

function search() {
    // Build search query object.
    var data = {};
    data.searchTerm = $('#search-term').val().replace(/\s\s+/g, ' ').trim();
    data.searchBy = [];
    $('#search-check').find('input').each(function() {
        if ($(this).is(':checked')) {
            data.searchBy.push($(this).attr('id'));
        }
    })
    data.filter = filter;
    console.log(data);

    // Send AJAX call. On response, display data.
}