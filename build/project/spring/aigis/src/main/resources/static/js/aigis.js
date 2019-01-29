const t = $(".top"), character = $(".character"), png = $(".png"), img = $(".img"),
    host = "http://assets.millennium-war.net/",
    characterTemp = '<div class="inline-block">\n<input name="character" type="radio" value="$name$">\n<span>$name$</span>\n</div>\n',
    pngTemp = '<div class="inline-block">\n<input name="png" type="radio" value="$url$" id="$name$">\n<span>$name$$count$</span>\n</div>\n';
let pathname = decodeURIComponent(location.pathname);
String.prototype.temp = function (obj) {
    return this.replace(/\$\w+\$/gi, function (matchs) {
        let returns = obj[matchs.replace(/\$/g, "")];
        return (returns + "") === "undefined" ? "" : returns;
    });
};
$(function () {
    history.replaceState(null, null);
    onpopstate = function () {
        let o = pathname.split("/"), n = (pathname = decodeURIComponent(location.pathname)).split("/"), b = false;
        if (o[3] !== n[3]) {
            b = true;
            $("[name='sex'][value='" + n[3] + "']").prop("checked", true);
        } else if (o[4] !== n[4]) {
            b = true;
            $("[name='rare'][value='" + n[4] + "']").prop("checked", true);
        } else if (o[5] !== n[5]) {
            b = true;
            $("[name='class'][value='" + n[5] + "']").prop("checked", true);
        }
        let c = function (deferred) {
            if (b) $.get("/aigis/character", {
                sex: $("[name=sex]:checked").val(),
                rare: $("[name=rare]:checked").val(),
                clazz: $("[name=class]:checked").val()
            }, function (result) {
                let htmlList = "";
                if (result.message === "success") result.data.forEach(function (o) {
                    htmlList += characterTemp.temp(o);
                });
                character.html(htmlList).find("input").change(function () {
                    u();
                });
                if (typeof n[6] === "undefined") {
                    png.html("");
                    $("img").prop("hidden", true);
                    deferred.reject();
                    return deferred;
                }
                deferred.resolve();
            }); else deferred.resolve();
            return deferred;
        };
        $.when(c($.Deferred())).done(function () {
            let u = function (deferred) {
                if (o[6] !== n[6]) {
                    $("[name='character'][value='" + n[6] + "']").prop("checked", true);
                    $.get("/aigis/png", {character: $("[name=character]:checked").val()}, function (result) {
                        let htmlList = "";
                        result.data.forEach(function (o) {
                            if (o.count === null) o.count = ""; else o.count = "(" + o.count + ")";
                            htmlList += pngTemp.temp(o);
                        });
                        png.html(htmlList).find("input").change(function () {
                            p();
                        });
                        deferred.resolve();
                    });
                } else deferred.resolve();
                return deferred;
            };
            $.when(u($.Deferred())).done(function () {
                $("[name='png'][id='" + n[7] + "']").prop("checked", true);
                let url = $("[name=png]:checked");
                let val = host + url.val();
                if (typeof $("img").prop("hidden", true).filter("[src='" + val + "']").prop("hidden", false)[0] === "undefined") {
                    img.append('<img src="' + val + '">\n');
                }
            });
        });
    };
    t.find("input").change(function () {
        c();
    });
    character.find("input").change(function () {
        u();
    });
    png.find("input").change(function () {
        p();
    });
});

function c() {
    $.get("/aigis/character", {
        sex: $("[name=sex]:checked").val(),
        rare: $("[name=rare]:checked").val(),
        clazz: $("[name=class]:checked").val()
    }, function (result) {
        let htmlList = "";
        if (result.message === "success") result.data.forEach(function (o) {
            htmlList += characterTemp.temp(o);
        });
        character.html(htmlList).find("input").change(function () {
            u();
        });
        $("[name='character']:eq(0)").prop("checked", true);
        u();
    });
}

function u() {
    let character = $("[name=character]:checked").val();
    if (typeof character === "undefined") {
        png.html("");
        $("img").prop("hidden", true);
        history.pushState(null, null, pathname = "/aigis/index/" + $("[name=sex]:checked").val() + "/" + $("[name=rare]:checked").val() + "/" + $("[name=class]:checked").val());
    } else $.get("/aigis/png", {character: character}, function (result) {
        let htmlList = "";
        result.data.forEach(function (o) {
            if (o.count === null) o.count = ""; else o.count = "(" + o.count + ")";
            htmlList += pngTemp.temp(o);
        });
        png.html(htmlList).find("input").change(function () {
            p();
        });
        $("[name='png']:eq(0)").prop("checked", true);
        p();
    });
}

function p() {
    let url = $("[name=png]:checked");
    let val = host + url.val();
    if (typeof $("img").prop("hidden", true).filter("[src='" + val + "']").prop("hidden", false)[0] === "undefined") {
        img.append('<img src="' + val + '">\n');
    }
    history.pushState(null, null, pathname = "/aigis/index/" + $("[name=sex]:checked").val() + "/" + $("[name=rare]:checked").val() + "/" + $("[name=class]:checked").val() + "/" + $("[name=character]:checked").val() + "/" + url.prop("id"));
}