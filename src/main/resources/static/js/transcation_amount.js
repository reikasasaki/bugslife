$(document).ready(function () {
  $("#upload_csv").on("click", function () {
    $("#status").text("ファイルをアップロード中...");

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
        $("#status").text("ファイルをアップロードしました");
        updateList(response);
      },
      error: function () {
        // ファイルアップロード失敗時のステータス表示
        $("#status").text("ファイルのアップロードに失敗しました");
      },
    });
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

    let deadLineDate = new Date(item["dueDate"]);
    console.log(deadLineDate);
    let deadLineYear = deadLineDate.getFullYear();
    let deadLineMonth = deadLineDate.getMonth();
    let deadLineDay = deadLineDate.getDay();

    console.log(deadLineYear + "-" + deadLineMonth + "-" + deadLineDay);

    tdBalanceOfPayment.textContent = item["plusMinus"] ? "収入" : "支出";
    tdExpensive.textContent = item["price"];
    tdDeadLine.textContent =
      deadLineYear + "年" + deadLineMonth + "月" + deadLineDay + "日";
    tdIsSupport.textContent = item["hasPaid"]
      ? item["plusMinus"]
        ? "入金済"
        : "支払済"
      : item["plusMinus"]
      ? "未納"
      : "未払";
    tdMemo.textContent = item["memo"];

    rowElement.appendChild(tdBalanceOfPayment);
    rowElement.appendChild(tdExpensive);
    rowElement.appendChild(tdDeadLine);
    rowElement.appendChild(tdIsSupport);
    rowElement.appendChild(tdMemo);

    tablebody.appendChild(rowElement);
  }
};
