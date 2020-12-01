$(document).ready(
    function () {
        $("#skip_ad").submit(
            function (event) {
                event.preventDefault();
                $.ajax({
                    type: "POST",
                    url: "/redirect",
                    data: $(this).serialize()
                });
            });
    });