var data = [
    //[렙제, 라피스, 라즐리, 1추 ~ 5추],
    [200, 342, 337, 150, 110, 75, 46, 21],
    [110, 102, 100, 23, 17, 12, 7, 4],
    [120, 105, 103, 24, 17, 12, 7, 4],
    [130, 107, 105, 32, 23, 16, 10, 5],
    [140, 114, 112, 34, 25, 17, 11, 5],
    [150, 121, 117, 36, 26, 18, 11, 5],
    [160, 139, 135, 41, 30, 21, 13, 6],
    [170, 173, 169, 64, 47, 32, 20, 9],
    [180, 207, 203, 76, 56, 38, 23, 11],
    [190, 297, 293, 131, 95, 65, 40, 18]
];
var table = document.getElementById('data_table');
var src = '';
for (var n = 0; n < data.length; n++) {
    src += '<tr align=center>' +
        '<td colspan=5 bgcolor=#E0E0E0><b>' + (n ? '라피스 & 라즐리 ' + n + '형' : '제네시스 라피스 & 라즐리') + '</b></td>' +
        '</tr>' +
        '<tr align=center>' +
        '<td rowspan=2><img src=\'./images/weapons/lapis_' + n + '.png\'></td>' +
        '<td bgcolor=#EEEEEE><b>공격력</b></td>' +
        '<td bgcolor=#EEEEEE><b>렙제</b></td>' +
        '<td bgcolor=#EEEEEE><b>공격력</b></td>' +
        '<td rowspan=2><img src=\'./images/weapons/lazuli_' + n + '.png\'></td>' +
        '</tr>' +
        '<tr align=center>' +
        '<td>' + data[n][1] + '</td>' +
        '<td>' + data[n][0] + '</td>' +
        '<td>' + data[n][2] + '</td>' +
        '</tr>' +
        '<tr align=center bgcolor=#EEEEEE>' +
        '<td width=20%><b>1추</b></td><td width=20%><b>2추</b></td><td width=20%><b>3추</b></td>' +
        '<td width=20%><b>4추</b></td><td width=20%><b>5추</b></td>' +
        '</tr>' +
        '<tr align=center>' +
        '<td>' + data[n][3] + '</td>' +
        '<td>' + data[n][4] + '</td>' +
        '<td>' + data[n][5] + '</td>' +
        '<td>' + data[n][6] + '</td>' +
        '<td>' + data[n][7] + '</td>' +
        '</tr>';
}
table.innerHTML = src;