$(document).ready(function () {
  
    $("#upload_csv").on("click", function () {

        $('#status').text('ファイルをアップロード中...');

         // FormDataオブジェクトを作成
         var formData = new FormData($('#uploadForm')[0]);

         var companyId = $("#companyId").text();
         console.log(companyId);

      $.ajax({
        url: '/api/transactionAmounts/' + companyId + '/upload_csv', 
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function () {
            // ファイルアップロード成功時のステータス表示
            $('#status').text('ファイルをアップロードしました');
            updateList();
        },
        error: function () {
            // ファイルアップロード失敗時のステータス表示
            $('#status').text('ファイルのアップロードに失敗しました');
        }
      })
    })


  });
  