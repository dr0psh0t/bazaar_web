ClassicEditor
    .create( document.querySelector( '#editor' ) )
    .catch( error => {
        console.error( error );
    }
);

$("#notificationFormSubmit").click(function() {

    if ($("#categoryDropDownList")[0].value > 0) {
        $("#notificationForm").submit();
    } else {
        alert('Please select a category');
    }
});