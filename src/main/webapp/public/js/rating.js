/**
 * Created by Matt on 5/11/17.
 */

/**
 * Loads the ratings from the template and puts the correct number of stars for the rating
 */
function loadRatings(){

    //floor the rating to get the number of stars
    var numStars=Math.floor(rating),
        $starSpan = $("#star-rating");

    var fillStar = $('<i class="fa fa-lg fa-star" aria-hidden="true"></i>'),
        emptyStar = $(' <i class="fa fa-lg fa-star-o" aria-hidden="true"></i>');
    $starSpan.empty();

    //iterate over the number of stars
    for(i=0;i<numStars;i++){
        var temp = fillStar.clone();
        temp.data("pos" ,i);
        temp.hover(hoverStar,endHover);
        temp.click(clickRating);
        $starSpan.append(temp);
    }
    for(i=numStars;i<5;i++){
        var temp = emptyStar.clone();
        temp.data("pos" ,i);
        temp.hover(hoverStar,endHover);
        temp.click(clickRating);
        $starSpan.append(temp);
    }
}

/**
 * function to handle highlighting stars
 */
function hoverStar() {
  //get the position of the star
  var pos = $(this).data("pos");
  $("#star-rating").find(".fa").each(function(){
    if($(this).data('pos')<=pos){
      $(this).removeClass("fa-star-o");
      $(this).addClass("fa-star");
    }
  });
}

function endHover(){
  //TODO: do this a better way
  $("#star-rating").find(".fa").each(function(){
    if($(this).data('pos')<rating){
      $(this).removeClass("fa-star-o");
      $(this).addClass("fa-star");
    }else{
      $(this).removeClass("fa-star");
      $(this).addClass("fa-star-o");
    }
  });
}

/**
 * Click handler for a rating star
 */
function clickRating(){
  var newRating = $(this).data('pos')+1;

  //TODO: assemble data properly
  var data = {};
  data.rating = newRating;
  //TODO: fix ajax request
  $.ajax({
    url:'/lesson/'+lesson_id+'/updaterating/'+newRating,
    method:'POST',
    contentType:'application/json',
    success: function(){
      rating = newRating;
    }
  });
}


function hideLesson(){
  var msg=$("#reportmsg").val();

  $.ajax({
    method:'POST',
    url:'/lesson/'+lesson_id+'/report',
    data:msg,
    contentType:"text/plain; charset=utf-8",
    success: function(data){
          window.location.href = "/user";
      }
  });
}
