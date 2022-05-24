$( document ).ready(function submitMsgNew() {
    $("#buttonS").click(function(){
        console.log( "ready!" );
        $.get( "/bin/messages", function( jsonObject ) {
            $.each(jsonObject, function(index,jsonItem){
                const pName = '<p>name: '+ jsonItem.name + '</p>';
                const pMessage = '<p>message: '+ jsonItem.message + '</p>';
                const pData = '<p>data: ' + jsonItem.date +'</p>';
                const li = '<li>' + pName + pMessage + pData + '</li>';
                $("#myResult").append(li);
            });
        });
    });
});
