
//NÚT 3 CHẤM
function ellipsisButton()
{

    $(document).on('click', '.c3', function () {

        var $this = $(this);
        var ellipsisId = $this.attr('id');
        var idComment = (ellipsisId.split("ellipsis_").toString().replace(",", ""));

        var editId = "edit_" + idComment;
        var acceptId = "accept_" + idComment;
        var cancelId = "cancel_" + idComment;
        var deleteId = "delete_" + idComment;

        var editElement = document.getElementById(editId);
        var acceptElement = document.getElementById(acceptId);
        var cancelElement = document.getElementById(cancelId);
        var deleteElement = document.getElementById(deleteId);

        if (editElement.style.display === "block")
        {
            editElement.style.display = "none";
            acceptElement.style.display = "none";
            cancelElement.style.display = "none";
            deleteElement.style.display = "none";
        } else {
            editElement.style.display = "block";
            deleteElement.style.display = "block";
        }

    });


}