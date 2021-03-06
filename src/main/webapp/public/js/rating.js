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
        //created filled stars for the numbe of ratings
        var temp = fillStar.clone();
        temp.data("pos" ,i);
        temp.hover(hoverStar,endHover);
        temp.click(clickRating);
        $starSpan.append(temp);
    }
    for(i=numStars;i<5;i++){
        //create outline stars for the stars that are greater than the rating
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
    }else{
      $(this).addClass("fa-star-o");
      $(this).removeClass("fa-star");
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


/**
 * Hides a lesson from the public and sends the user an alert that their lesson was hidden
 */
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


/**
 * Opens a prefilled email so the user can tell all the mods about a bad lesson
 */
function sendModMail(){
  var subject = "Issue with Lesson " + lesson_id,
    body = "There was an issue with a lesson, check it out at : " + window.location.href,
  url = 'mailto:codo.programming@gmail.com?subject='+encodeURIComponent(subject)+'&body='+encodeURIComponent(body);

  //open the mailto url to send an email to mod
  window.open(url);
}

