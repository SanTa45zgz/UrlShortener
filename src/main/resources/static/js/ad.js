$(document).ready(
    function () {
        $("#skip_ad").submit(
            function (event) {
                event.preventDefault();
                $.ajax({
                    type: "POST",
                    url: "/redirect", // devuelve la URI buena
                    data: $(this).serialize(),
                    success: function(data) {
                        window.open(data, "_self")
                    }
                });
            });
    });