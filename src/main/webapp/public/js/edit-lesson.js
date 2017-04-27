/**
 * Created by Matt on 4/27/17.
 * Javascript for the edit lesson page
 */
$(function(){
    $( "#sortable" ).sortable({
        placeholder: "ui-state-highlight"
    });
    $( "#sortable" ).disableSelection();
});
