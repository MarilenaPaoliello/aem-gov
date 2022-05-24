(function(document){
   
    const DIALOG_RESOURCE_TYPE ="gov/components/submitOnDbCopy";

    function isTargetDialog($formElement, dlgResourceType){
        const resourceType = $formElement.find("input[name='./sling:resourceType']").val();
       return resourceType === dlgResourceType;
    }
    
    $(document).on("dialog-ready", function (){
        const $formElement = $(this).find("coral-dialig form.foundation-form").val();
        if(!isTargetDialog($formElement, DIALOG_RESOURCE_TYPE)){
            console.log("not working at all!!");
        }else{
            console.log("working very well!!");
        }
    })
}(document,Granite.$));

