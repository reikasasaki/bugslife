$(document).ready(function () {
  $("#upload_csv").on("click", function () {
    $("#status").text("取引金額CSV：取込中");

    // FormDataオブジェクトを作成
    var formData = new FormData($("#uploadForm")[0]);

    var companyId = $("#companyId").text();
    console.log(companyId);

    $.ajax({
      url: "/api/transactionAmounts/" + companyId + "/upload_csv",
      type: "POST",
      data: formData,
      processData: false,
      contentType: false,
      success: function (response) {
        // ファイルアップロード成功時のステータス表示
        $("#status").text("取引金額CSV：完了");
        updateList(response);
      },
      error: function () {
        // ファイルアップロード失敗時のステータス表示
        $("#status").text("取引金額CSV：エラー");
      },
    });
    $('input[type="file"]').val(null);
  });
});

const updateList = (response) => {
  let tablebody = document.getElementById("tablebody");

  while (tablebody.lastChild) {
    tablebody.removeChild(tablebody.lastChild);
  }

  for (let index in response) {
    let item = response[index];
    console.log(item);

    let rowElement = document.createElement("tr");
    let tdBalanceOfPayment = document.createElement("td");
    let tdExpensive = document.createElement("td");
    let tdDeadLine = document.createElement("td");
    let tdIsSupport = document.createElement("td");
    let tdMemo = document.createElement("td");
    let tdButton = document.createElement("td");
    let aDetails = document.createElement("a");
    let aEdit = document.createElement("a");
    let aDelete = document.createElement("a");

    let deadLineDate = new Date(item["dueDate"]);
    console.log(deadLineDate);
    let deadLineYear = deadLineDate.getFullYear();
    let deadLineMonth = deadLineDate.getMonth();
    let deadLineDay = deadLineDate.getDay();

    console.log(deadLineYear + "-" + deadLineMonth + "-" + deadLineDay);

    tdBalanceOfPayment.textContent = item["plusMinus"] ? "収入" : "支出";
    tdExpensive.textContent = item["price"];
    tdDeadLine.textContent =
      deadLineYear + "-" + deadLineMonth + "-" + deadLineDay ;
    tdIsSupport.textContent = item["hasPaid"]
      ? item["plusMinus"]
        ? "入金済"
        : "支払済"
      : item["plusMinus"]
      ? "未納"
      : "未払";
    tdMemo.textContent = item["memo"];

    $(aDetails).addClass("btn btn-primary");
    aDetails.href = "/transactionAmounts/" + item["id"];
    aDetails.textContent = "詳細";

    $(aEdit).addClass("btn btn-secondary");
    aEdit.href = "/transactionAmounts/" + item["id"] + "/edit";
    aEdit.textContent = "編集";

    $(aDelete).addClass("btn btn-danger");
    $(aDelete).attr("data-id",item["id"]);
    aDelete.textContent = "削除";

    $(aDelete).click(function () {
      let id = $(this).attr("data-id");
      let deleteButton = $(this);
      $.ajax({
        url: "/api/transactionAmounts/" + id,
        type: "DELETE",
      success: function () {
        deleteButton.closest("tr").remove();
      },
      error: function () {
        console.log("削除に失敗しました");
      },
      })
    })    

    tdButton.appendChild(aDetails);
    tdButton.appendChild(aEdit);
    tdButton.appendChild(aDelete);

    rowElement.appendChild(tdBalanceOfPayment);
    rowElement.appendChild(tdExpensive);
    rowElement.appendChild(tdDeadLine);
    rowElement.appendChild(tdIsSupport);
    rowElement.appendChild(tdMemo);
    rowElement.appendChild(tdButton);

    tablebody.appendChild(rowElement);
  }
};
