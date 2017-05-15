$(document).ready(function() {
  init();
});

function init() {
    // On filter, refresh data.
    $( '#filter-select' ).change(function() {      
        filter = $(this).val();
        search();
    });

    // On sort, refresh data.
    $('a[data-toggle="pill"]').on('click', function(e) {
        if ($(this).attr('id') === sortBy) {
            asc = !asc;
            if ($(this).attr('id') !== 'none') {
                toggleArrow($(this));
            }
            search();
        }
    }).on('shown.bs.tab', function(e) {
        sortBy = $(e.target).attr('id');
        asc = (sortBy === 'rating') ? false : true;
        $(this).closest('ul').find('.fa').hide();
        if ($(e.target).attr('id') !== 'none') {
            toggleArrow($(this));
        }
        search();
    });
}

function toggleArrow(node) {
    var arrow = $(node).find('.fa');
    arrow.show();
    if (asc) {
        arrow.removeClass('fa-sort-desc').addClass('fa-sort-asc');
    }
    else {
        arrow.addClass('fa-sort-desc').removeClass('fa-sort-asc');
    }
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

    // Send AJAX call. On response, display data.
    $.ajax({
        headers: { 
          'Accept': 'application/json',
          'Content-Type': 'application/json' 
        },
        'type': 'POST',
        'url': '/search/request',
        'data': JSON.stringify(data),
        'dataType': 'json',
        'success': function(data) {
            populateLessons(data);
        }
    });
}

// Populate the search results with lessons.
function populateLessons(data) {
    // Empty the search results and populate them.
    var searchResults = $('#search-results');
    searchResults.empty();
    searchResults.append($('<h2>Search Results</h2>').addClass('text-center'));

    for (_i = 0; _i < data.length; _i += 2) {
        // Build a row, which should have two lessons.
        var resultRow = $('<div>').addClass('row');

        // Append up to two lessons.
        for (_j = _i; _j < data.length && _j < _i + 2; _j++) {
            // Build ratings span.
            rating = data[_j].rating;

            var ratingsSpan = $('<span>').attr('data-toggle', 'tooltip')
                                    .attr('title', rating + ' stars');

            for (_r = 0; _r < 5; _r++) {
                if (_r < rating - 1) {
                    ratingsSpan.append($('<i>').addClass('fa').addClass('fa-star'));
                }
                else if (_r < rating - .5) {
                    ratingsSpan.append($('<i>').addClass('fa').addClass('fa-star-half-o'));
                }
                else {
                    ratingsSpan.append($('<i>').addClass('fa').addClass('fa-star-o'));
                }
            }

            // Build title box.
            var titleBox = $('<div>').addClass('title-box')
                                .addClass('col-4')
            titleBox.append(ratingsSpan)
                    .append($('<span>').text(data[_j].title))
                    .append($('<span>').text(data[_j].percent_complete + '%'));

            // Build description box.
            var descriptionBox = $('<div>').addClass('col-8')
                                    .addClass('description-box')
                                    .text(data[_j].description);

            // Build the actual display element.
            var lessonBox = $('<div>').addClass('row')
                                .addClass('lesson-box')
                                .append(titleBox)
                                .append(descriptionBox);

            // Create the link.
            var link = (data[_j].creator_id == user_id) ? '/createlesson/' : '/lesson/' 
            link += data[_j].lesson_id;

            var lessonLink = $('<a>').addClass('lesson-link')
                                .attr('href', link)
                                .append(lessonBox);

            // Add col-6 styling.
            var lessonDiv = $('<div>').addClass('col-6')
                                .addClass('align-items-center')
                                .append(lessonLink);

            // Add it to the row.
            lessonDiv.appendTo(resultRow);
        }

        // Append the row to the results.
        searchResults.append(resultRow);
    }
}