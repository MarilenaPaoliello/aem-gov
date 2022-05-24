(function (document, $, ns) {
    "use strict";
    $(document).on("click", ".cq-dialog-submit", function (e) {
        
        e.stopPropagation();
        e.preventDefault();
        
        var $form = $(this).closest("form.foundation-form"),
            $inputs = $form.find("[name$='./fullName']"),
            $input = null,
            nameid,
            isError = false,
            patterns = {
                nameadd: /[0-9]/
            };
            $inputs.each(function(index, input) {
                $input = $(input);
                nameid=$input.val();
                if(nameid != "" && patterns.nameadd.test(nameid) && (nameid != null)) {
                isError=true;
                $input.css("border", "2px solid #FF0000");
                ns.ui.helpers.prompt({
                    title: Granite.I18n.get("Invalid Input"),
                    message: "Please Enter just characters",
                    actions: [{
                        id: "CANCEL",
                        text: "CANCEL",
                        className: "coral-Button"
                    }],
                    callback: function (actionId) {
                        if (actionId === "CANCEL") {
                        }
                    }
                });
                }else
                    {
                    $input.css("border", "");
                    }
            });
            
            if(!isError){
                $form.submit();
            } 
    

/* $(document).on("dialog-ready", function(event){
    event.preventDefault();
    const $formElement = $(this).find("coral-dialog form.foundation-form");
    if(!isTargetDialog($formElement, DIALOG_RESOURCE_TYPE)){
        console.log("not working at all!!");
    }else{
        console.log("working very well!!");
    }
}) */
    });
}(document,Granite.$, Granite.author));

