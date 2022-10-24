$(document).ready(function() {
    $("#deleteCustomerButton").click(function() {
        var $customerFields = $("#deleteCustomerForm").find('input[name="customers"]:checked');
        if (!$customerFields.length) {
            $('#errorDeleteCustomerModal').modal('show');
            return false
        }

        return true;
    });

    $("#deleteNotificationButton").click(function() {
        var $notificationFields = $("#deleteNotificationForm").find('input[name="notifications"]:checked');
        if (!$notificationFields.length) {
            $('#errorDeleteNotificationModal').modal('show');
            return false
        }

        return true;
    });

    $(".alert-success").delay(4000).slideUp(200, function() {
        console.log("success closing");
        $(this).alert('close');
    });

    $(".alert-failure").delay(4000).slideUp(200, function() {
        console.log("failure closing");
        $(this).alert('close');
    });
});