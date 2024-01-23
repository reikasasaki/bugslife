$(document).ready(function () {
  // 全チェック切り替え
  $("#all_check").on("click", function () {
    // 各行のチェックボックスの切り替え
    $(".row_check").prop("checked", this.checked);
  });

  // 各行のチェック切り替え
  $(".row_check").on("click", function () {
    var checkBox = ".row_check";
    var boxCount = $(checkBox).length; //全チェックボックスの数を取得
    var checked = $(checkBox + ":checked").length; //チェックされているチェックボックスの数を取得
    if (checked === boxCount) {
      $("#all_check").prop("checked", true);
    } else {
      $("#all_check").prop("checked", false);
    }

    
    const is_checked = $(this).prop("checked");
    $(this).closest("tr").find("input").not(".row_check").each(function (index, element) {
      if (is_checked) {
        $(element).prop("disabled", false);
      } else {
        $(element).prop("disabled", true);
      }
    })
  });

  $("#update_button").on("click", function () {
    const data = $(this).closest('form').serializeArray();
    $.ajax({
      url: '/orders/shipping', // 更新処理
      type: 'POST',
      data: data
    })
    .done( (result) => {
      $('.result').html(result);
    })
    .fail( (jqXHR, textStatus, errorThrown) => {
      alert('Ajax通信に失敗しました。');
      console.log("jqXHR          : " + jqXHR.status); // HTTPステータスを表示
      console.log("textStatus     : " + textStatus);    // タイムアウト、パースエラーなどのエラー情報を表示
      console.log("errorThrown    : " + errorThrown.message); // 例外情報を表示
    })
    .always( (result) => {
      if($('#answer').val() == '小松菜奈'){
        console.log('あなたは正しい');
      }else{
        console.log('あなたは間違っている');
      }
    });
  })
});
