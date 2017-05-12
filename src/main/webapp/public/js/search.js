$(document).ready(function() {
  init();
});

function init() {
    // On filter, refresh data.
    $( '#filter-select' ).change(function() {      
        filter = $(this).val();
        console.log(filter);
        search();
    });



    $('a[data-toggle="pill"]').on('click', function(e) {
        if ($(this).attr('id') === sortBy) {
            asc = !asc;
            search();
        }
    }).on('shown.bs.tab', function(e) {
        console.log('different');
        sortBy = $(e.target).attr('id');
        asc = true;
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
    data.sortBy = sortBy;
    data.asc = asc;
    console.log(data);

    // Send AJAX call. On response, display data.
}