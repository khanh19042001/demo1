var headsMap = {};
var colSlot = [];
var trs = arguments[0].querySelectorAll(".fa-head");
for(var row = 0; row < trs.length; row++){
    var ths = trs[row].childNodes;
    var actualColIndex = 0;
    for(var col = 0; col < ths.length; col++){
        var cell = ths[col];
        if(window.getComputedStyle(cell).display == "none"){
            continue;
        }
        var title = cell.innerText.toLowerCase().replace("ð", "đ");
        var collspan = cell.getAttribute("colspan");
        collspan = collspan ? parseInt(collspan) : 1;
        for (var i = 0; i < collspan; i++) {
            if (row > 0) {
                if (colSlot.length > 0) {
                    // từ row thứ 2 chở đi tìm các slot còn trống trong colspan của parent và cộng dồn header của child vào
                    var slotIndex = colSlot.shift(0);
                    headsMap[slotIndex] = headsMap[slotIndex] + "//" + title;
                    if (collspan > 1) colSlot.push(slotIndex);
                }
            } else {
                // row đầu thì chỉ việc push colspan slot
                if (collspan > 1) colSlot.push(actualColIndex);
                headsMap[actualColIndex] = title;
                actualColIndex++;
            }
        }
    }
}

return headsMap;