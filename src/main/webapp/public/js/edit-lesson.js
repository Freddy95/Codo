/**
 * Created by Matt on 4/27/17.
 * Javascript for the edit lesson page
 */
$(function(){
    //get the elements we need to build the session object
    var $publicRadio = $("#publicradio") ,
        $descriptionText=$("#description-text"),
        $submiteDescButton=$("#submit-description-button"),
        $titleText=$("#title-edit-form"),
        $titleSubmitButton=$("#title-submit-btn"),
        $taskList=$("#tasks-list"),
        $titleEditDone=$("#title-done"),
        $titleEditButton=$("#title-button"),
        $showTitleEditFormButton=$("#title-edit"),
        $titleNoButton=$("#title-no-btn"),
        $titleEditForm=$("#title-edit-form"),
        $sortable=$(".sortable");

    //handler for the edit title button being clicked
    $showTitleEditFormButton.click(function () {
        $titleEditDone.hide();
        $titleEditForm.show();

    });

    //handler to hide the title edit form
    $titleNoButton.click(function(){
        $titleEditForm.hide();
        $titleEditDone.show();
    });

    $sortable.sortable({
        placeholder: "ui-state-highlight"
    });
    $sortable.disableSelection();
});
