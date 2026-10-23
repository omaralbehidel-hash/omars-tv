'use strict';
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g = Object.create((typeof Iterator === "function" ? Iterator : Object).prototype);
    return g.next = verb(0), g["throw"] = verb(1), g["return"] = verb(2), typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (g && (g = 0, op[0] && (_ = 0)), _) try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};
var __read = (this && this.__read) || function (o, n) {
    var m = typeof Symbol === "function" && o[Symbol.iterator];
    if (!m) return o;
    var i = m.call(o), r, ar = [], e;
    try {
        while ((n === void 0 || n-- > 0) && !(r = i.next()).done) ar.push(r.value);
    }
    catch (error) { e = { error: error }; }
    finally {
        try {
            if (r && !r.done && (m = i["return"])) m.call(i);
        }
        finally { if (e) throw e.error; }
    }
    return ar;
};
var __spreadArray = (this && this.__spreadArray) || function (to, from, pack) {
    if (pack || arguments.length === 2) for (var i = 0, l = from.length, ar; i < l; i++) {
        if (ar || !(i in from)) {
            if (!ar) ar = Array.prototype.slice.call(from, 0, i);
            ar[i] = from[i];
        }
    }
    return to.concat(ar || Array.prototype.slice.call(from));
};
var __values = (this && this.__values) || function(o) {
    var s = typeof Symbol === "function" && Symbol.iterator, m = s && o[s], i = 0;
    if (m) return m.call(o);
    if (o && typeof o.length === "number") return {
        next: function () {
            if (o && i >= o.length) o = void 0;
            return { value: o && o[i++], done: !o };
        }
    };
    throw new TypeError(s ? "Object is not iterable." : "Symbol.iterator is not defined.");
};
var e_1, _a;
var $ = function (s, r) {
    if (r === void 0) { r = document; }
    return r.querySelector(s);
};
var $$ = function (s, r) {
    if (r === void 0) { r = document; }
    return __spreadArray([], __read(r.querySelectorAll(s)), false);
};
var KEY = 'omars.';
var store = {
    get: function (k, d) { try {
        var v = localStorage.getItem(KEY + k);
        return v == null ? d : JSON.parse(v);
    }
    catch (e) {
        return d;
    } },
    set: function (k, v) { try {
        localStorage.setItem(KEY + k, JSON.stringify(v));
    }
    catch (e) { } },
    del: function (k) { try {
        localStorage.removeItem(KEY + k);
    }
    catch (e) { } }
};
var DEFAULTS = {
    h24: false, secs: true, date: true, wx: true, prayers: true, list: true, listLeft: false,
    fx: false, burn: true, digits: true, autoplay: true, alert: true, beep: false, sleepFx: true, boot: false,
    clock: 1, listw: 20, dim: 0.25, hijri: 0, vol: 80,
    q: 'auto', acc: 'gold', city: 'madinah', unit: 'c', method: 4, asr: 0, gps: null
};
var S = Object.assign({}, DEFAULTS, store.get('settings', {}));
if (!['gold', 'blue', 'orange', 'white'].includes(S.acc) || S.acc === 'blue')
    S.acc = 'gold';
var SAVED = Object.assign({}, S);
var CITIES = [
    { id: 'madinah', n: 'المدينة المنورة', lat: 24.4686, lon: 39.6142, tz: 'Asia/Riyadh' },
    { id: 'makkah', n: 'مكة المكرمة', lat: 21.3891, lon: 39.8579, tz: 'Asia/Riyadh' },
    { id: 'jeddah', n: 'جدة', lat: 21.4858, lon: 39.1925, tz: 'Asia/Riyadh' },
    { id: 'riyadh', n: 'الرياض', lat: 24.7136, lon: 46.6753, tz: 'Asia/Riyadh' },
    { id: 'dammam', n: 'الدمام', lat: 26.4207, lon: 50.0888, tz: 'Asia/Riyadh' },
    { id: 'abha', n: 'أبها', lat: 18.2164, lon: 42.5053, tz: 'Asia/Riyadh' },
    { id: 'tabuk', n: 'تبوك', lat: 28.3838, lon: 36.555, tz: 'Asia/Riyadh' },
    { id: 'buraidah', n: 'بريدة', lat: 26.326, lon: 43.975, tz: 'Asia/Riyadh' },
    { id: 'dubai', n: 'دبي', lat: 25.2048, lon: 55.2708, tz: 'Asia/Dubai' },
    { id: 'kuwait', n: 'الكويت', lat: 29.3759, lon: 47.9774, tz: 'Asia/Kuwait' },
    { id: 'doha', n: 'الدوحة', lat: 25.2854, lon: 51.531, tz: 'Asia/Qatar' },
    { id: 'amman', n: 'عمّان', lat: 31.9454, lon: 35.9284, tz: 'Asia/Amman' },
    { id: 'cairo', n: 'القاهرة', lat: 30.0444, lon: 31.2357, tz: 'Africa/Cairo' },
    { id: 'gps', n: 'موقعي الحالي', lat: 0, lon: 0, tz: Intl.DateTimeFormat().resolvedOptions().timeZone || 'UTC' }
];
var city = function () {
    var c = CITIES.find(function (x) { return x.id === S.city; }) || CITIES[0];
    if (c.id === 'gps')
        return S.gps ? Object.assign({}, c, S.gps) : CITIES[0];
    return c;
};
var METHODS = [[4, 'أم القرى'], [3, 'رابطة العالم الإسلامي'], [5, 'الهيئة المصرية'], [2, 'ISNA'], [1, 'جامعة العلوم – كراتشي']];
var AR = '٠١٢٣٤٥٦٧٨٩';
var ar = function (n) { return S.digits ? String(n).replace(/\d/g, function (d) { return AR[d]; }) : String(n); };
var p2 = function (n) { return String(n).padStart(2, '0'); };
function toast(msg, ms) {
    if (ms === void 0) { ms = 3000; }
    var t = $('#toast');
    t.textContent = msg;
    t.style.display = 'block';
    clearTimeout(toast.h);
    toast.h = setTimeout(function () { return t.style.display = 'none'; }, ms);
}
var mk = function (n, c, u, t) {
    if (u === void 0) { u = ''; }
    if (t === void 0) { t = 'yt'; }
    return ({ n: n, c: c, u: u, t: t, l: '', v: 80, f: false });
};
var YT = 'https://www.youtube.com/';
var H = 'hls', Q = 'https://qurango.net/radio/', K = 'https://live.kwikmotion.com/';
var DEFAULT_LISTS = function () { return ({
    tv: [
        mk('قناة القرآن الكريم', 'إسلامية', 'https://cdn-globecast.akamaized.net/live/eds/saudi_quran/hls_roku/index.m3u8', H),
        mk('قناة السنة النبوية', 'إسلامية', 'https://cdn-globecast.akamaized.net/live/eds/saudi_sunnah/hls_roku/index.m3u8', H),
        mk('الجزيرة', 'أخبار', 'https://live-hls-web-aja.getaj.net/AJA/index.m3u8', H),
        mk('الجزيرة 2', 'أخبار', 'https://live-hls-web-aja2-gcp.thehlive.com/AJA2/index.m3u8', H),
        mk('العربية', 'أخبار', 'https://av.alarabiya.net/alarabiapublish/alarabiya.smil/playlist.m3u8', H),
        mk('الحدث', 'أخبار', 'https://av.alarabiya.net/alarabiapublish/alhadath.smil/playlist.m3u8', H),
        mk('Sky News Arabia', 'أخبار', 'https://stream.skynewsarabia.com/hls/sna.m3u8', H),
        mk('القرآن الكريم (يوتيوب)', 'يوتيوب', YT + 'channel/UCos52azQNBgW63_9uDJoPDA'),
        mk('السنة النبوية (يوتيوب)', 'يوتيوب', YT + 'channel/UCNF8YJDral7UmSisQ5XZMGw'),
        mk('الجزيرة (يوتيوب)', 'يوتيوب', YT + 'channel/UCfiwzLy-8yKzIbsmZTzxDgw'),
        mk('العربية (يوتيوب)', 'يوتيوب', YT + 'channel/UCrj5BGAhtWxDfqbza9T9hqA'),
        mk('الحدث (يوتيوب)', 'يوتيوب', YT + 'channel/UCYcogyJ0RBPHvfw3X-5L0bQ'),
        mk('سكاي نيوز عربية (يوتيوب)', 'يوتيوب', YT + 'channel/UCIJXOvggjKtCagMfxvcCzAA')
    ],
    radio: [
        mk('إذاعة نداء الإسلام', 'إذاعة', K + 'sbrksanedaradiolive/ksanedaradio/playlist.m3u8', 'audio'),
        mk('إذاعة القرآن الكريم (السعودية)', 'إذاعة', K + 'sbrksaquranradiolive/ksaquranradio/playlist.m3u8', 'audio'),
        mk('إذاعة الرياض', 'إذاعة', K + 'sbrksariyadhradiolive/ksariyadhradio/playlist.m3u8', 'audio'),
        mk('إذاعة جدة', 'إذاعة', K + 'sbrksajeddahradiolive/ksajeddahradio/playlist.m3u8', 'audio'),
        mk('إذاعة الإخبارية', 'إذاعة', K + 'sbrksaikhbariyaradiolive/srpikhbariyaradio/playlist.m3u8', 'audio'),
        mk('ترتيل متواصل', 'إذاعة', Q + 'tarateel', 'audio'),
        mk('ماهر المعيقلي', 'إذاعة', Q + 'maher_almuaiqly', 'audio'),
        mk('ياسر الدوسري', 'إذاعة', Q + 'yasser_aldosari', 'audio'),
        mk('فارس عباد', 'إذاعة', Q + 'fares_abbad', 'audio'),
        mk('عبدالباسط عبدالصمد', 'إذاعة', Q + 'abdulbasit_abdulsamad', 'audio'),
        mk('تفسير القرآن', 'إذاعة', Q + 'tafseer', 'audio')
    ]
}); };
var L = store.get('lists', null);
if (!L || !Array.isArray(L.tv) || !Array.isArray(L.radio))
    L = DEFAULT_LISTS();
if (store.get('lists.v', 0) < 8) {
    var old_1 = new Set(['الجزيرة 2 (مباشر)', 'قناة القرآن الكريم (مكة المكرمة)', 'قناة السنة النبوية (المدينة المنورة)', 'الجزيرة 1', 'الجزيرة 2', 'العربية', 'الحدث', 'الشرق للأخبار', 'BBC News عربي', 'Sky News Arabia', 'إذاعة نداء الإسلام', 'إذاعة القرآن الكريم', 'قناة القرآن الكريم (يوتيوب)', 'قناة السنة النبوية (يوتيوب)', 'الشرق للأخبار (يوتيوب)', 'BBC News عربي (يوتيوب)']);
    var def_1 = DEFAULT_LISTS();
    ['tv', 'radio'].forEach(function (k) { L[k] = def_1[k].concat(L[k].filter(function (c) { return !old_1.has(c.n) && !def_1[k].some(function (d) { return d.n === c.n; }); })); });
    store.set('lists.v', 8);
    store.set('lists', L);
    store.set('last', null);
}
var saveLists = function () { return store.set('lists', L); };
var P = {
    mode: 'tv', idx: { tv: 0, radio: 0 }, fav: { tv: false, radio: false },
    playing: false, buffering: false, muted: false, vol: 80,
    kind: null, el: null, hls: null, an: null, token: 0, timers: [], rt: 0, fb: 0, last: null
};
var cur = function () { return L[P.mode][P.idx[P.mode]]; };
function parseYT(u) {
    var x;
    try {
        x = new URL(u);
    }
    catch (e) {
        return null;
    }
    var h = x.hostname.replace(/^www\.|^m\./, '');
    var seg = x.pathname.split('/').filter(Boolean);
    var id = null, ch = null;
    if (h === 'youtu.be')
        id = seg[0];
    else if (h === 'youtube.com' || h === 'youtube-nocookie.com') {
        if (x.searchParams.get('v'))
            id = x.searchParams.get('v');
        else if (['live', 'embed', 'shorts', 'v'].includes(seg[0]) && seg[1] && seg[1] !== 'live_stream')
            id = seg[1];
        else if (seg[0] === 'channel' && /^UC[\w-]{20,}$/.test(seg[1] || ''))
            ch = seg[1];
        else if (seg[0] === 'embed' && seg[1] === 'live_stream' && /^UC[\w-]{20,}$/.test(x.searchParams.get('channel') || ''))
            ch = x.searchParams.get('channel');
    }
    else
        return null;
    if (id && /^[\w-]{6,}$/.test(id))
        return { id: id };
    if (ch)
        return { ch: ch };
    return null;
}
var isYT = function (u) { return /^https?:\/\/(www\.|m\.)?(youtube(-nocookie)?\.com|youtu\.be)\//i.test(u); };
function validate(type, u) {
    if (!u)
        return '';
    if (type === 'yt') {
        if (!isYT(u))
            return 'الرابط ليس رابط YouTube. اختر النوع «بث مباشر متوافق» لروابط البث الأخرى.';
        if (!parseYT(u))
            return 'رابط YouTube غير مدعوم. استخدم رابط الفيديو المباشر، أو رابط القناة بصيغة youtube.com/channel/UC…';
        return '';
    }
    if (!/^https?:\/\/[^\s]+$/i.test(u))
        return 'الرابط يجب أن يبدأ بـ https://';
    return '';
}
var AC = null;
function clearTimers() { P.timers.forEach(clearTimeout); P.timers = []; }
function later(fn, ms, tok) { P.timers.push(setTimeout(function () { if (tok === P.token)
    fn(); }, ms)); }
function stopPlayback(keepUi) {
    P.token++;
    clearTimers();
    if (P.hls) {
        try {
            P.hls.destroy();
        }
        catch (e) { }
        P.hls = null;
    }
    if (P.el) {
        try {
            P.el.pause();
            P.el.removeAttribute('src');
            P.el.load && P.el.load();
        }
        catch (e) { }
        P.el = null;
    }
    $('#viewport').innerHTML = '';
    P.an = null;
    P.kind = null;
    P.playing = false;
    P.buffering = false;
    if (!keepUi)
        hideOverlay();
    ui();
}
function showOverlay(title, msg, b1, f1, b2, f2) {
    $('#ovt').textContent = title;
    $('#ovm').textContent = msg;
    var a = $('#ovb1'), b = $('#ovb2');
    a.textContent = b1 || '';
    a.style.display = b1 ? '' : 'none';
    a.onclick = f1 || null;
    b.textContent = b2 || '';
    b.style.display = b2 ? '' : 'none';
    b.onclick = f2 || null;
    $('#overlay').classList.add('on');
    P.playing = false;
    ui();
    setTimeout(function () { var t = a.style.display === 'none' ? b : a; if (!document.body.classList.contains('sleeping') && $('#overlay').classList.contains('on'))
        t.focus(); }, 30);
}
function hideOverlay() { $('#overlay').classList.remove('on'); }
function failure(tok, title, msg, retry) {
    if (retry === void 0) { retry = true; }
    if (tok !== P.token)
        return;
    var m = P.mode, i = P.idx[m];
    if (P.fb > 0) {
        P.fb--;
        var arr = L[m];
        var j = i;
        for (var n = 0; n < arr.length; n++) {
            j = (j + 1) % arr.length;
            if (arr[j].u)
                break;
        }
        if (j !== i && arr[j].u) {
            toast('تعذر تشغيل ' + arr[i].n + ' — جارٍ تجربة ' + arr[j].n, 3500);
            play(m, j, { auto: true });
            return;
        }
    }
    showOverlay(title, msg, retry ? 'إعادة المحاولة' : '', function () { return play(m, i); }, 'التالي', function () { return step(1); });
}
function play(m, i, opts) {
    if (opts === void 0) { opts = {}; }
    stopPlayback();
    if (!opts.auto) {
        P.rt = 0;
        P.fb = 0;
    }
    var list = L[m];
    if (!list.length) {
        render();
        return;
    }
    i = Math.max(0, Math.min(i, list.length - 1));
    P.mode = m;
    P.idx[m] = i;
    setMode(m, true);
    var it = list[i];
    P.last = { m: m, i: i };
    store.set('last', P.last);
    P.vol = (it.v != null ? it.v : S.vol);
    render();
    ui();
    var tok = P.token;
    if (!it.u) {
        showOverlay(it.n, 'لم تُضف رابط البث لهذه القناة بعد. أضف الرابط الرسمي مرة واحدة، وستعمل تلقائيًا في كل مرة تفتح فيها التطبيق.', 'إضافة الرابط', function () { return openManage(m, i); }, 'التالي', function () { return step(1); });
        return;
    }
    if (!navigator.onLine) {
        failure(tok, 'لا يوجد اتصال بالإنترنت', 'تحقق من اتصال التلفزيون بالشبكة ثم أعد المحاولة.');
        return;
    }
    var err = validate(isYT(it.u) ? 'yt' : it.t === 'yt' ? 'yt' : 'hls', it.u);
    if (err) {
        failure(tok, 'الرابط غير صالح', err, false);
        return;
    }
    P.kind = isYT(it.u) ? 'yt' : (m === 'radio' ? 'audio' : 'hls');
    P.playing = true;
    P.buffering = true;
    ui();
    if (P.kind === 'yt')
        startYT(it, tok);
    else if (P.kind === 'hls')
        startVideo(it, tok);
    else
        startAudio(it, tok);
}
function ytSend(func, args) {
    var f = $('#viewport iframe');
    if (!f || !f.contentWindow)
        return;
    try {
        f.contentWindow.postMessage(JSON.stringify({ event: 'command', func: func, args: args || [] }), '*');
    }
    catch (e) { }
}
function startYT(it, tok) {
    var r = parseYT(it.u);
    var base = r.id ? 'https://www.youtube.com/embed/' + r.id + '?' : 'https://www.youtube.com/embed/live_stream?channel=' + r.ch + '&';
    var src = base + 'autoplay=1&playsinline=1&controls=0&rel=0&modestbranding=1&iv_load_policy=3&disablekb=1&fs=0&enablejsapi=1' + (/^https?:/.test(location.origin) ? '&origin=' + encodeURIComponent(location.origin) : '');
    var f = document.createElement('iframe');
    f.setAttribute('allow', 'autoplay; encrypted-media; picture-in-picture');
    f.setAttribute('referrerpolicy', 'strict-origin-when-cross-origin');
    f.tabIndex = -1;
    f.src = src;
    f.onload = function () {
        var n = 0;
        var hs = function () {
            if (tok !== P.token || n++ > 12)
                return;
            try {
                f.contentWindow.postMessage(JSON.stringify({ event: 'listening', id: 1, channel: 'widget' }), '*');
            }
            catch (e) { }
            later(hs, 700, tok);
        };
        hs();
    };
    $('#viewport').appendChild(f);
    P.el = null;
    later(function () { if (P.buffering)
        failure(tok, 'تعذر بدء البث', 'لم يبدأ التشغيل خلال المهلة. قد لا تكون القناة مباشرة الآن، أو يمنع المالك تشغيلها خارج يوتيوب. تحقق من الرابط من «إدارة القنوات».'); }, 25000);
}
window.addEventListener('message', function (e) {
    if (P.kind !== 'yt' || !/youtube(-nocookie)?\.com$/.test(new URL(e.origin === 'null' ? 'http://x' : e.origin).hostname))
        return;
    var d = e.data;
    if (typeof d === 'string') {
        try {
            d = JSON.parse(d);
        }
        catch (x) {
            return;
        }
    }
    if (!d || typeof d !== 'object')
        return;
    var tok = P.token;
    if (d.event === 'onError') {
        var c = +d.info;
        var msg = (c === 101 || c === 150 || c === 153) ? 'صاحب القناة لا يسمح بتشغيل هذا البث خارج يوتيوب. استخدم رابط البث الرسمي المتوافق من «إدارة القنوات».'
            : (c === 100 ? 'هذا الفيديو غير متاح أو لم يعد مباشرًا.' : 'الرابط غير صالح للتشغيل.');
        if (c === 101 || c === 150 || c === 153) {
            var it = L[P.mode][P.idx[P.mode]];
            var yu_1 = it && it.u;
            showOverlay('تعذر تشغيل القناة هنا', msg + ' يمكنك فتحها مباشرة في يوتيوب.', 'فتح في يوتيوب', function () { try {
                window.open(yu_1, '_blank');
            }
            catch (x) { } }, 'التالي', function () { return step(1); });
        }
        else
            failure(tok, 'تعذر تشغيل القناة', msg);
    }
    else if (d.event === 'onStateChange') {
        var s = +d.info;
        if (s === 1) {
            P.fb = 0;
            P.buffering = false;
            P.playing = true;
            hideOverlay();
            ytSend('setVolume', [P.vol]);
            ytSend(P.muted ? 'mute' : 'unMute');
        }
        else if (s === 2) {
            P.playing = false;
        }
        else if (s === 3) {
            P.buffering = true;
        }
        ui();
    }
    else if (d.event === 'infoDelivery' && d.info && d.info.playerState != null && d.info.playerState === 1 && P.buffering) {
        P.buffering = false;
        P.playing = true;
        hideOverlay();
        ytSend('setVolume', [P.vol]);
        ytSend(P.muted ? 'mute' : 'unMute');
        ui();
    }
});
function startVideo(it, tok) {
    var v = document.createElement('video');
    v.playsInline = true;
    v.autoplay = true;
    v.muted = P.muted;
    v.volume = P.vol / 100;
    v.tabIndex = -1;
    $('#viewport').appendChild(v);
    P.el = v;
    bindMedia(v, it, tok);
    var isM3 = /\.m3u8(\?|$)/i.test(it.u);
    if (isM3 && window.Hls && Hls.isSupported()) {
        var h_1 = new Hls({ lowLatencyMode: false, maxBufferLength: 30, capLevelToPlayerSize: false, abrEwmaDefaultEstimate: 12e6, abrBandWidthFactor: 0.95, abrBandWidthUpFactor: 0.8, enableWorker: true });
        h_1.on(Hls.Events.MANIFEST_PARSED, function (_, d) { var top = d.levels.length - 1; if (S.q === 'max')
            h_1.currentLevel = top;
        else if (S.q === 'low')
            h_1.currentLevel = 0;
        else
            h_1.startLevel = top; });
        P.hls = h_1;
        h_1.loadSource(it.u);
        h_1.attachMedia(v);
        h_1.on(Hls.Events.ERROR, function (_, d) {
            if (tok !== P.token || !d.fatal)
                return;
            if (d.type === Hls.ErrorTypes.NETWORK_ERROR)
                retry(tok, function () { return h_1.startLoad(); });
            else if (d.type === Hls.ErrorTypes.MEDIA_ERROR)
                h_1.recoverMediaError();
            else
                failure(tok, 'تعذر تشغيل البث', 'صيغة البث غير مدعومة أو الرابط غير صالح.');
        });
    }
    else {
        v.src = it.u;
        v.load();
    }
    v.play && v.play().catch(function () { });
}
function startAudio(it, tok, noCors) {
    var a = new Audio();
    if (!noCors)
        a.crossOrigin = 'anonymous';
    a.preload = 'auto';
    a.muted = P.muted;
    a.volume = P.vol / 100;
    P.el = a;
    bindMedia(a, it, tok, noCors);
    a.src = it.u;
    a.play().catch(function () { });
}
function bindMedia(el, it, tok, noCors) {
    el.addEventListener('playing', function () {
        if (tok !== P.token)
            return;
        P.playing = true;
        P.buffering = false;
        P.rt = 0;
        P.fb = 0;
        hideOverlay();
        ui();
        if (!P.an && (P.kind === 'hls' ? P.hls : (P.kind === 'audio' && !noCors)))
            tap(el);
    });
    el.addEventListener('waiting', function () { if (tok === P.token) {
        P.buffering = true;
        ui();
    } });
    el.addEventListener('pause', function () { if (tok === P.token && !el.ended) {
        P.playing = false;
        ui();
    } });
    el.addEventListener('error', function () {
        if (tok !== P.token)
            return;
        if (P.kind === 'audio' && !noCors) {
            stopForRetry();
            startAudio(it, P.token, true);
            return;
        }
        retry(tok, function () { var m = P.mode, i = P.idx[m]; play(m, i, { auto: true }); });
    });
    later(function () { if (P.buffering)
        failure(tok, 'تعذر الاتصال بالبث', 'لم يبدأ التشغيل خلال المهلة. تحقق من الرابط ومن اتصال الإنترنت.'); }, 25000, tok);
}
function stopForRetry() {
    P.token++;
    clearTimers();
    if (P.el) {
        try {
            P.el.pause();
            P.el.removeAttribute('src');
        }
        catch (e) { }
        P.el = null;
    }
    P.an = null;
}
function retry(tok, fn) {
    if (tok !== P.token)
        return;
    if (P.rt >= 3) {
        failure(tok, 'انقطع البث', 'تعذر الاستمرار في التشغيل بعد عدة محاولات. تحقق من الرابط ومن الاتصال بالإنترنت.');
        return;
    }
    var wait = 2000 * Math.pow(2, P.rt++);
    P.buffering = true;
    ui();
    later(fn, wait, tok);
}
function tap(el) {
    try {
        AC = AC || new (window.AudioContext || window.webkitAudioContext)();
        if (AC.state === 'suspended')
            AC.resume();
        var src = AC.createMediaElementSource(el), an = AC.createAnalyser();
        an.fftSize = 128;
        an.smoothingTimeConstant = 0.8;
        src.connect(an);
        an.connect(AC.destination);
        P.an = an;
    }
    catch (e) {
        P.an = null;
    }
}
function togglePlay() {
    if (!P.kind) {
        play(P.mode, P.idx[P.mode]);
        return;
    }
    if (P.kind === 'yt') {
        ytSend(P.playing ? 'pauseVideo' : 'playVideo');
        P.playing = !P.playing;
    }
    else if (P.el) {
        if (P.el.paused)
            P.el.play().catch(function () { });
        else
            P.el.pause();
    }
    ui();
}
function stopAll() { stopPlayback(); toast('تم إيقاف التشغيل'); }
function setVol(v) {
    P.vol = Math.max(0, Math.min(100, v));
    if (P.kind === 'yt')
        ytSend('setVolume', [P.vol]);
    else if (P.el)
        P.el.volume = P.vol / 100;
    ui();
}
function toggleMute() {
    P.muted = !P.muted;
    if (P.kind === 'yt')
        ytSend(P.muted ? 'mute' : 'unMute');
    else if (P.el)
        P.el.muted = P.muted;
    ui();
}
function step(d) {
    var arr = L[P.mode];
    if (!arr.length)
        return;
    var i = P.idx[P.mode];
    for (var n = 0; n < arr.length; n++) {
        i = (i + d + arr.length) % arr.length;
        if (!P.fav[P.mode] || arr[i].f)
            break;
    }
    play(P.mode, i);
}
function setMode(m, silent) {
    P.mode = m;
    document.body.classList.toggle('mode-radio', m === 'radio');
    document.body.classList.toggle('mode-tv', m === 'tv');
    $$('#seg button').forEach(function (b) { return b.classList.toggle('on', b.dataset.m === m); });
    if (!silent)
        render();
    ui();
}
function ui() {
    var it = cur();
    $('#bplay').innerHTML = P.playing && !P.buffering
        ? '<svg viewBox="0 0 24 24"><path class="f" d="M7 5h3.5v14H7zM13.5 5H17v14h-3.5z"/></svg>'
        : '<svg viewBox="0 0 24 24"><path class="f" d="M8 5l11 7-11 7z"/></svg>';
    $('#bplay').setAttribute('aria-label', P.playing ? 'إيقاف مؤقت' : 'تشغيل');
    $('#bmute').innerHTML = P.muted || P.vol === 0
        ? '<svg viewBox="0 0 24 24"><path d="M4 9v6h4l5 4V5L8 9zM17 9l4 6M21 9l-4 6"/></svg>'
        : '<svg viewBox="0 0 24 24"><path d="M4 9v6h4l5 4V5L8 9zM16.5 8.5a5 5 0 0 1 0 7M19 6a8.5 8.5 0 0 1 0 12"/></svg>';
    $('#vlab').textContent = ar(P.vol) + '٪';
    $('#live').classList.toggle('on', P.playing && !P.buffering && P.mode === 'tv' && !document.body.classList.contains('aud'));
    $('#audname').textContent = it ? it.n : '';
    $('#baud').style.display = P.mode === 'tv' ? '' : 'none';
    $('#baud').classList.toggle('on', document.body.classList.contains('aud'));
    $('#baud').textContent = document.body.classList.contains('aud') ? 'عرض الفيديو' : 'مؤشر صوتي';
    $('#bfav').classList.toggle('on', P.fav[P.mode]);
}
function initial(n) { return n.replace(/\(.*\)/, '').replace(/^قناة\s+/, '').replace(/^إذاعة\s+/, '').trim().split(/\s+/)[0].slice(0, 9); }
function esc(s) { return String(s).replace(/[&<>"']/g, function (c) { return ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]); }); }
function render() {
    var m = P.mode, arr = L[m];
    $('#lhead').textContent = (m === 'tv' ? 'القنوات ' : 'المحطات ') + ar(arr.length);
    var fi = document.activeElement && document.activeElement.closest && document.activeElement.closest('#list') ? document.activeElement.dataset.i : null;
    var html = '';
    arr.forEach(function (c, i) {
        if (P.fav[m] && !c.f)
            return;
        var isCur = i === P.idx[m], ok = !!c.u;
        var st = isCur && P.playing && !P.buffering ? 'قيد العرض · يعمل' : isCur && P.buffering && P.kind ? 'جارٍ الاتصال…' : ok ? (m === 'tv' ? 'بث مباشر' : 'بث صوتي') : 'أضف الرابط';
        html += "<button class=\"item f ".concat(isCur ? 'cur' : '', " ").concat(ok ? '' : 'nolink', "\" data-act=\"sel\" data-i=\"").concat(i, "\">\n      <span class=\"lg\">").concat(c.l ? "<img src=\"".concat(esc(c.l), "\" alt=\"\" onerror=\"this.remove()\">") : '').concat(c.l ? '' : esc(initial(c.n)), "</span>\n      <span class=\"nm\"><b>").concat(esc(c.n), "</b><small>").concat(st, "</small></span><span class=\"no\">").concat(ar(i + 1), "</span></button>");
    });
    $('#list').innerHTML = html || '<div class="empty">لا توجد عناصر. أضف من «إدارة القنوات».</div>';
    if (fi != null) {
        var n = $("#list [data-i=\"".concat(fi, "\"]"));
        if (n)
            n.focus();
    }
}
var fmtParts = function (loc, o) { return new Intl.DateTimeFormat(loc, o); };
var _gF, _hF, _wF, _lastDay = '';
function dateFmts() {
    var nu = S.digits ? 'arab' : 'latn';
    _gF = fmtParts('ar-u-ca-gregory-nu-' + nu, { day: 'numeric', month: 'long', year: 'numeric' });
    _hF = fmtParts('ar-u-ca-islamic-umalqura-nu-' + nu, { day: 'numeric', month: 'long', year: 'numeric' });
    _wF = fmtParts('ar-u-ca-gregory-nu-' + nu, { weekday: 'long' });
}
function hijriText(d) {
    var x = new Date(d.getTime() + S.hijri * 864e5);
    try {
        var p_1 = _hF.formatToParts(x);
        var g = function (t) { return (p_1.find(function (q) { return q.type === t; }) || {}).value || ''; };
        return g('day') + ' ' + g('month') + ' ' + g('year') + ' هـ';
    }
    catch (e) {
        return '';
    }
}
function timeText(h, m) {
    var H = S.h24 ? h : (h % 12 || 12);
    return { t: ar(S.h24 ? p2(H) : H) + ':' + ar(p2(m)), ap: S.h24 ? '' : (h < 12 ? 'ص' : 'م') };
}
function tickClock() {
    var d = new Date(), h = d.getHours(), m = d.getMinutes(), s = d.getSeconds();
    var _a = timeText(h, m), t = _a.t, ap = _a.ap;
    if ($('#ct').textContent !== t) {
        $('#ct').textContent = t;
        $('#sct').textContent = t;
    }
    $('#cs').textContent = ar(p2(s));
    $('#cap').textContent = ap;
    $('#scap').textContent = ap;
    var day = d.toDateString();
    if (day !== _lastDay) {
        _lastDay = day;
        $('#dday').textContent = _wF.format(d);
        var dd = _gF.format(d) + '  •  ' + hijriText(d);
        $('#ddate').textContent = dd;
        $('#sdd').textContent = _wF.format(d) + '  •  ' + dd;
    }
}
var PN = [['Fajr', 'الفجر'], ['Sunrise', 'الشروق'], ['Dhuhr', 'الظهر'], ['Asr', 'العصر'], ['Maghrib', 'المغرب'], ['Isha', 'العشاء']];
var PT = null, PTMsg = '', tomorrowFajr = null, ptBusy = false, lastPtDate = '';
function nowIn(tz) {
    var p;
    try {
        p = new Intl.DateTimeFormat('en-GB', { timeZone: tz, hourCycle: 'h23', year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit' }).formatToParts(new Date());
    }
    catch (e) {
        p = new Intl.DateTimeFormat('en-GB', { hourCycle: 'h23', year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit' }).formatToParts(new Date());
    }
    var g = function (t) { return +p.find(function (x) { return x.type === t; }).value; };
    return { y: g('year'), mo: g('month'), d: g('day'), sec: g('hour') * 3600 + g('minute') * 60 + g('second') };
}
var dmy = function (y, mo, d) { return p2(d) + '-' + p2(mo) + '-' + y; };
var toSec = function (s) { var m = /(\d{1,2}):(\d{2})/.exec(s || ''); return m ? (+m[1]) * 3600 + (+m[2]) * 60 : null; };
function getDay(c, dstr) {
    return __awaiter(this, void 0, void 0, function () {
        var key, hit, url, ctl, to, r, j, tm_1, out;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0:
                    key = "pt.".concat(c.lat.toFixed(2), ".").concat(c.lon.toFixed(2), ".").concat(S.method, ".").concat(S.asr, ".").concat(dstr);
                    hit = store.get(key, null);
                    if (hit)
                        return [2, hit];
                    url = "https://api.aladhan.com/v1/timings/".concat(dstr, "?latitude=").concat(c.lat, "&longitude=").concat(c.lon, "&method=").concat(S.method, "&school=").concat(S.asr);
                    ctl = new AbortController();
                    to = setTimeout(function () { return ctl.abort(); }, 12000);
                    _a.label = 1;
                case 1:
                    _a.trys.push([1, , 4, 5]);
                    return [4, fetch(url, { signal: ctl.signal })];
                case 2:
                    r = _a.sent();
                    if (!r.ok)
                        throw new Error(r.status);
                    return [4, r.json()];
                case 3:
                    j = _a.sent();
                    tm_1 = j.data.timings;
                    out = { tz: (j.data.meta && j.data.meta.timezone) || c.tz, t: PN.map(function (_a) {
                            var _b = __read(_a, 1), k = _b[0];
                            return toSec(tm_1[k]);
                        }), date: dstr };
                    if (out.t.some(function (v) { return v == null; }))
                        throw new Error('bad');
                    try {
                        Object.keys(localStorage).filter(function (k) { return k.startsWith(KEY + 'pt.') && !k.endsWith(dstr); }).slice(20).forEach(function (k) { return localStorage.removeItem(k); });
                    }
                    catch (e) { }
                    store.set(key, out);
                    store.set('pt.last', out);
                    return [2, out];
                case 4:
                    clearTimeout(to);
                    return [7];
                case 5: return [2];
            }
        });
    });
}
function refreshTimes() {
    return __awaiter(this, void 0, void 0, function () {
        var c, n, today, nn, t, tm, e_2, e_3, last;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0:
                    if (ptBusy)
                        return [2];
                    ptBusy = true;
                    c = city();
                    _a.label = 1;
                case 1:
                    _a.trys.push([1, 7, , 8]);
                    n = nowIn(c.tz);
                    return [4, getDay(c, dmy(n.y, n.mo, n.d))];
                case 2:
                    today = _a.sent();
                    PT = today;
                    PTMsg = '';
                    lastPtDate = dmy(n.y, n.mo, n.d);
                    tomorrowFajr = null;
                    _a.label = 3;
                case 3:
                    _a.trys.push([3, 5, , 6]);
                    nn = nowIn(today.tz);
                    t = new Date(Date.UTC(nn.y, nn.mo - 1, nn.d + 1));
                    return [4, getDay(c, dmy(t.getUTCFullYear(), t.getUTCMonth() + 1, t.getUTCDate()))];
                case 4:
                    tm = _a.sent();
                    tomorrowFajr = tm.t[0];
                    return [3, 6];
                case 5:
                    e_2 = _a.sent();
                    return [3, 6];
                case 6: return [3, 8];
                case 7:
                    e_3 = _a.sent();
                    last = store.get('pt.last', null);
                    if (last) {
                        PT = last;
                        PTMsg = 'مواقيت محفوظة — تعذر التحديث';
                    }
                    else {
                        PT = null;
                        PTMsg = 'تعذر جلب المواقيت. تحقق من الاتصال.';
                    }
                    setTimeout(refreshTimes, 60000);
                    return [3, 8];
                case 8:
                    ptBusy = false;
                    pk = '';
                    tickPrayers(true);
                    return [2];
            }
        });
    });
}
var pk = '', alerted = '', alertTimer = 0;
function nextIdx(sec) {
    for (var i = 0; i < 6; i++) {
        if (i === 1)
            continue;
        if (PT.t[i] > sec)
            return i;
    }
    return -1;
}
function tickPrayers(force) {
    var box = $('#prayers');
    var c = city();
    if (!PT) {
        if (force || pk !== 'none') {
            pk = 'none';
            box.innerHTML = "<div class=\"pc nxt\"><strong>\u0645\u0648\u0627\u0642\u064A\u062A \u0627\u0644\u0635\u0644\u0627\u0629</strong><em>".concat(esc(PTMsg || 'جارٍ التحميل…'), "</em><small>").concat(esc(c.n), "</small></div>") + PN.map(function (n) { return "<div class=\"pc\"><span>".concat(n[1], "</span><b>\u2014</b></div>"); }).join('');
        }
        return;
    }
    var n = nowIn(PT.tz);
    var today = dmy(n.y, n.mo, n.d);
    if (!PTMsg && today !== lastPtDate && !ptBusy) {
        refreshTimes();
    }
    var ni = nextIdx(n.sec), left, tom = false;
    if (ni < 0) {
        ni = 0;
        tom = true;
        left = 86400 - n.sec + (tomorrowFajr != null ? tomorrowFajr : PT.t[0]);
    }
    else
        left = PT.t[ni] - n.sec;
    var fmt = function (h) { return timeText(Math.floor(h / 3600), Math.floor(h % 3600 / 60)); };
    var key = [ni, S.h24, S.digits, PT.date, PTMsg, c.id, tom].join('|');
    if (force || key !== pk) {
        pk = key;
        box.innerHTML = "<div class=\"pc nxt ".concat(PTMsg ? 'stale' : '', "\"><strong id=\"nxl\"></strong><em>").concat(tom ? 'مواقيت الغد' : 'الصلاة القادمة', "</em><div class=\"cd\" id=\"nxc\"></div><small>").concat(esc(c.n)).concat(PTMsg ? ' · ' : '', "<small class=\"st\">").concat(esc(PTMsg), "</small></small></div>") +
            PN.map(function (nm, i) { var f = fmt(PT.t[i]); var past = !tom && PT.t[i] <= n.sec; return "<div class=\"pc ".concat(i === ni && !tom ? 'next' : '', " ").concat(past ? 'past' : '', "\"><span>").concat(nm[1], "</span><b>").concat(f.t, "<small>").concat(f.ap, "</small></b></div>"); }).join('');
        $('#sp').innerHTML = PN.map(function (nm, i) { var f = fmt(PT.t[i]); return "<div class=\"".concat(i === ni ? 'n' : '', "\">").concat(nm[1], "<b>").concat(f.t, "</b></div>"); }).join('');
    }
    var L2 = Math.max(0, left);
    $('#nxl').textContent = 'متبقٍ على صلاة ' + PN[ni][1];
    $('#nxc').textContent = ar(p2(L2 / 3600 | 0)) + ':' + ar(p2(L2 % 3600 / 60 | 0)) + ':' + ar(p2(L2 % 60));
    for (var i = 0; i < 6; i++) {
        if (i === 1)
            continue;
        var diff = n.sec - PT.t[i];
        if (diff >= 0 && diff < 5 && alerted !== today + i) {
            alerted = today + i;
            if (S.alert)
                showAlert('حان الآن موعد صلاة ' + PN[i][1]);
        }
    }
}
function showAlert(txt) {
    var b = $('#alertbar');
    b.textContent = txt;
    b.classList.add('on');
    clearTimeout(alertTimer);
    alertTimer = setTimeout(function () { return b.classList.remove('on'); }, 60000);
    if (S.beep)
        chime();
}
function chime() {
    try {
        AC = AC || new (window.AudioContext || window.webkitAudioContext)();
        if (AC.state === 'suspended')
            AC.resume();
        [0, .5, 1].forEach(function (t, k) {
            var o = AC.createOscillator(), g = AC.createGain();
            o.frequency.value = [660, 880, 990][k];
            o.connect(g);
            g.connect(AC.destination);
            var at = AC.currentTime + t;
            g.gain.setValueAtTime(0.0001, at);
            g.gain.exponentialRampToValueAtTime(0.25, at + .05);
            g.gain.exponentialRampToValueAtTime(0.0001, at + .6);
            o.start(at);
            o.stop(at + .65);
        });
    }
    catch (e) { }
}
var ICONS = {
    sun: '<svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M2 12h2M20 12h2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4"/></svg>',
    moon: '<svg viewBox="0 0 24 24"><path d="M20 14.5A8.5 8.5 0 0 1 9.5 4a8.5 8.5 0 1 0 10.5 10.5z"/></svg>',
    cloud: '<svg viewBox="0 0 24 24"><path d="M7 18a4.5 4.5 0 0 1-.5-9A6 6 0 0 1 18 10a4 4 0 0 1 0 8z"/></svg>',
    part: '<svg viewBox="0 0 24 24"><circle cx="8" cy="8" r="3"/><path d="M8 2v1M2 8h1M3.8 3.8l.7.7M12.2 3.8l-.7.7"/><path d="M9 20a4 4 0 0 1-.4-8A5 5 0 0 1 18 13a3.500 3.500 0 0 1 0 7z"/></svg>',
    rain: '<svg viewBox="0 0 24 24"><path d="M7 15a4.500 4.500 0 0 1-.5-9A6 6 0 0 1 18 7a4 4 0 0 1 0 8zM8 18l-1 3M12 18l-1 3M16 18l-1 3"/></svg>',
    storm: '<svg viewBox="0 0 24 24"><path d="M7 15a4.500 4.500 0 0 1-.5-9A6 6 0 0 1 18 7a4 4 0 0 1 0 8zM12 14l-2 4h3l-2 4"/></svg>',
    snow: '<svg viewBox="0 0 24 24"><path d="M12 3v18M4 7.500l16 9M20 7.500l-16 9"/></svg>',
    fog: '<svg viewBox="0 0 24 24"><path d="M4 9h16M3 13h18M6 17h12"/></svg>'
};
function wcode(c, day) {
    if (c === 0)
        return day ? ['صافٍ', 'sun'] : ['صافٍ', 'moon'];
    if (c === 1)
        return day ? ['غالبًا صافٍ', 'sun'] : ['غالبًا صافٍ', 'moon'];
    if (c === 2)
        return ['غائم جزئيًا', 'part'];
    if (c === 3)
        return ['غائم', 'cloud'];
    if (c === 45 || c === 48)
        return ['ضباب', 'fog'];
    if (c >= 51 && c <= 57)
        return ['رذاذ', 'rain'];
    if ((c >= 61 && c <= 67) || (c >= 80 && c <= 82))
        return ['مطر', 'rain'];
    if ((c >= 71 && c <= 77) || c === 85 || c === 86)
        return ['ثلج', 'snow'];
    if (c >= 95)
        return ['عاصفة رعدية', 'storm'];
    return ['غائم', 'cloud'];
}
var WX = null, wxExp = false;
function refreshWx() {
    return __awaiter(this, void 0, void 0, function () {
        var c, u, url, ctl, to, r, e_4, old;
        var _a;
        return __generator(this, function (_b) {
            switch (_b.label) {
                case 0:
                    c = city(), u = S.unit === 'f' ? 'fahrenheit' : 'celsius';
                    url = "https://api.open-meteo.com/v1/forecast?latitude=".concat(c.lat, "&longitude=").concat(c.lon, "&current=temperature_2m,apparent_temperature,relative_humidity_2m,wind_speed_10m,weather_code,is_day&daily=temperature_2m_max,temperature_2m_min,weather_code&timezone=auto&forecast_days=3&temperature_unit=").concat(u, "&wind_speed_unit=kmh");
                    ctl = new AbortController();
                    to = setTimeout(function () { return ctl.abort(); }, 12000);
                    _b.label = 1;
                case 1:
                    _b.trys.push([1, 4, 5, 6]);
                    return [4, fetch(url, { signal: ctl.signal })];
                case 2:
                    r = _b.sent();
                    if (!r.ok)
                        throw new Error(r.status);
                    _a = {};
                    return [4, r.json()];
                case 3:
                    WX = (_a.j = _b.sent(), _a.at = new Date(), _a.city = c.id, _a.unit = S.unit, _a);
                    store.set('wx', WX);
                    return [3, 6];
                case 4:
                    e_4 = _b.sent();
                    if (!WX) {
                        old = store.get('wx', null);
                        if (old && old.city === c.id && old.unit === S.unit) {
                            WX = old;
                            WX.at = new Date(WX.at);
                        }
                    }
                    WX = WX || null;
                    if (WX)
                        WX.stale = true;
                    else
                        WX = { fail: true };
                    return [3, 6];
                case 5:
                    clearTimeout(to);
                    return [7];
                case 6:
                    renderWx();
                    return [2];
            }
        });
    });
}
function renderWx() {
    var el = $('#wx'), c = city();
    if (!WX || WX.fail) {
        el.classList.remove('exp');
        el.innerHTML = "<b>".concat(esc(c.n), "</b><small>\u0627\u0644\u0637\u0642\u0633 \u063A\u064A\u0631 \u0645\u062A\u0627\u062D \u2014 \u062A\u062D\u0642\u0642 \u0645\u0646 \u0627\u0644\u0627\u062A\u0635\u0627\u0644</small>");
        $('#sw').textContent = '';
        return;
    }
    var j = WX.j, cu = j.current, _a = __read(wcode(cu.weather_code, cu.is_day), 2), txt = _a[0], ic = _a[1];
    var sym = S.unit === 'f' ? '°F' : '°';
    var at = new Date(WX.at), t = function (x) { return '<bdi>' + ar(Math.round(x)) + '</bdi>'; };
    var upd = 'آخر تحديث ' + ar(p2(at.getHours()) + ':' + p2(at.getMinutes())) + (WX.stale ? ' (تعذر التحديث)' : '');
    var dn = ['اليوم', 'غدًا', 'بعد غد'];
    el.classList.toggle('exp', wxExp);
    el.innerHTML = "<b>".concat(esc(c.n), "</b>").concat(ICONS[ic], "<span class=\"tp\">").concat(ar(Math.round(cu.temperature_2m))).concat(sym, "</span><small>").concat(txt, "<br>\u0627\u0644\u0645\u062D\u0633\u0648\u0633\u0629 ").concat(t(cu.apparent_temperature), "\u00B0</small>") +
        "<div class=\"wxmore\"><small>\u0631\u0637\u0648\u0628\u0629<br>".concat(t(cu.relative_humidity_2m), "\u066A</small><small>\u0627\u0644\u0631\u064A\u0627\u062D<br>").concat(t(cu.wind_speed_10m), " \u0643\u0645/\u0633</small>") +
        j.daily.time.map(function (_, i) { return "<small>".concat(dn[i], "<br>").concat(t(j.daily.temperature_2m_max[i]), "\u00B0 / ").concat(t(j.daily.temperature_2m_min[i]), "\u00B0</small>"); }).join('') + "<small>".concat(upd, "</small></div>");
    el.title = upd;
    $('#sw').innerHTML = "".concat(esc(c.n), " \u00B7 ").concat(txt, " \u00B7 <bdi>").concat(ar(Math.round(cu.temperature_2m))).concat(sym, "</bdi> \u00B7 \u0631\u0637\u0648\u0628\u0629 <bdi>").concat(ar(Math.round(cu.relative_humidity_2m)), "\u066A</bdi>");
}
var viz = $('#viz'), vx = viz.getContext('2d');
var vlast = 0;
function drawViz(t) {
    requestAnimationFrame(drawViz);
    if (t - vlast < 40)
        return;
    vlast = t;
    var visible = (document.body.classList.contains('aud') || P.mode === 'radio') && !document.body.classList.contains('sleeping') && !document.hidden;
    if (!visible)
        return;
    var w = viz.clientWidth, h = viz.clientHeight;
    if (viz.width !== w)
        viz.width = w;
    if (viz.height !== h)
        viz.height = h;
    vx.clearRect(0, 0, w, h);
    var n = 48, bw = w / n;
    var data = null;
    var live = P.playing && !P.buffering && !P.muted && P.vol > 0;
    if (live && P.an) {
        data = new Uint8Array(P.an.frequencyBinCount);
        P.an.getByteFrequencyData(data);
    }
    var real = data && data.some(function (v) { return v > 0; });
    var col = getComputedStyle(document.body).getPropertyValue('--accent') || '#0a84ff';
    vx.fillStyle = col.trim();
    for (var i = 0; i < n; i++) {
        var a = real ? data[Math.floor(i * data.length * 0.7 / n)] / 255 : 0;
        var bh = Math.max(3, a * h);
        vx.globalAlpha = real ? .9 : .25;
        var x = i * bw + bw * .18, r = bw * .32;
        vx.beginPath();
        if (vx.roundRect)
            vx.roundRect(x, (h - bh) / 2, bw * .64, bh, r);
        else
            vx.rect(x, (h - bh) / 2, bw * .64, bh);
        vx.fill();
    }
    var note = $('#audnote');
    var msg = !live ? (P.kind ? 'متوقف مؤقتًا' : '') : (real ? '' : 'المؤشر غير متاح لهذا المصدر — الصوت يعمل بشكل طبيعي');
    if (note.textContent !== msg)
        note.textContent = msg;
}
var fx = $('#fx'), fc = fx.getContext('2d'), stars = Array.from({ length: 90 }, function () { return ({ x: Math.random(), y: Math.random(), r: Math.random() * 1.2 + .3, p: Math.random() * 6 }); });
var flast = 0;
function drawFx(t) {
    var e_5, _a;
    requestAnimationFrame(drawFx);
    if (!S.fx || document.hidden || document.body.classList.contains('sleeping') || document.body.classList.contains('full') || t - flast < 100)
        return;
    flast = t;
    if (fx.width !== fx.clientWidth) {
        fx.width = fx.clientWidth;
        fx.height = fx.clientHeight;
    }
    fc.clearRect(0, 0, fx.width, fx.height);
    fc.fillStyle = '#fff';
    try {
        for (var stars_1 = __values(stars), stars_1_1 = stars_1.next(); !stars_1_1.done; stars_1_1 = stars_1.next()) {
            var s = stars_1_1.value;
            fc.globalAlpha = .15 + .3 * (0.5 + 0.5 * Math.sin(t / 2500 + s.p));
            fc.beginPath();
            fc.arc(s.x * fx.width, s.y * fx.height, s.r, 0, 7);
            fc.fill();
        }
    }
    catch (e_5_1) { e_5 = { error: e_5_1 }; }
    finally {
        try {
            if (stars_1_1 && !stars_1_1.done && (_a = stars_1.return)) _a.call(stars_1);
        }
        finally { if (e_5) throw e_5.error; }
    }
}
var openSheetId = null, prevFocus = null;
function openSheet(id) {
    if (openSheetId)
        closeSheet(true);
    prevFocus = document.activeElement;
    openSheetId = id;
    var s = $('#sheet-' + id);
    s.classList.add('on');
    s.setAttribute('aria-hidden', 'false');
    if (id === 'settings') {
        rows();
    }
    if (id === 'manage')
        renderManage();
    if (id === 'sleep')
        updateTimerLabel();
    setTimeout(function () { var f = $('button:not([disabled])', s); if (f)
        f.focus(); }, 30);
}
function closeSheet(quiet) {
    if (!openSheetId)
        return;
    var s = $('#sheet-' + openSheetId);
    s.classList.remove('on');
    s.setAttribute('aria-hidden', 'true');
    openSheetId = null;
    if (!quiet && prevFocus && document.contains(prevFocus))
        prevFocus.focus();
}
var CFG = [
    { g: 'العرض' },
    { k: 'h24', l: 'نظام 24 ساعة', t: 't' }, { k: 'secs', l: 'إظهار الثواني', t: 't' }, { k: 'date', l: 'إظهار التاريخ', t: 't' },
    { k: 'wx', l: 'إظهار الطقس', t: 't' }, { k: 'prayers', l: 'إظهار مواقيت الصلاة', t: 't' }, { k: 'list', l: 'إظهار قائمة القنوات', t: 't' },
    { k: 'digits', l: 'الأرقام العربية (٠١٢)', t: 't' },
    { k: 'clock', l: 'حجم الساعة', t: 'n', min: .7, max: 1.4, st: .1 }, { k: 'listw', l: 'عرض قائمة القنوات', t: 'n', min: 14, max: 28, st: 2 },
    { k: 'listLeft', l: 'موضع القائمة', t: 'c', o: [[false, 'يمين'], [true, 'يسار']] },
    { k: 'acc', l: 'لون التمييز', t: 'c', o: [['gold', 'ذهبي'], ['blue', 'أزرق'], ['orange', 'برتقالي'], ['white', 'أبيض']] },
    { k: 'fx', l: 'خلفية النجوم المتحركة', t: 't' }, { k: 'burn', l: 'منع احتراق الشاشة (إزاحة بطيئة)', t: 't' },
    { g: 'المدينة والمواقيت' },
    { k: 'city', l: 'المدينة', t: 'c', o: CITIES.map(function (c) { return [c.id, c.n]; }) },
    { k: 'method', l: 'طريقة حساب الصلاة', t: 'c', o: METHODS },
    { k: 'asr', l: 'مذهب العصر', t: 'c', o: [[0, 'الجمهور'], [1, 'الحنفي']] },
    { k: 'hijri', l: 'إزاحة التاريخ الهجري (أيام)', t: 'n', min: -2, max: 2, st: 1 },
    { k: 'alert', l: 'تنبيه مرئي عند دخول الوقت', t: 't' }, { k: 'beep', l: 'تنبيه صوتي عند دخول الوقت', t: 't' },
    { k: 'unit', l: 'وحدة الحرارة', t: 'c', o: [['c', 'مئوية'], ['f', 'فهرنهايت']] },
    { g: 'التشغيل والصوت' },
    { k: 'q', l: 'جودة الصورة', t: 'c', o: [['auto', 'تلقائي (يبدأ بأعلى جودة)'], ['max', 'الأعلى دائمًا'], ['low', 'الأقل (إنترنت ضعيف)']] },
    { k: 'autoplay', l: 'تشغيل آخر قناة تلقائيًا عند الفتح', t: 't' },
    { k: 'vol', l: 'مستوى الصوت الافتراضي', t: 'n', min: 0, max: 100, st: 10 },
    { k: 'dim', l: 'سطوع واجهة وضع النوم', t: 'n', min: .05, max: 1, st: .05, pct: 1 },
    { k: 'sleepFx', l: 'حركة الإزاحة في وضع النوم', t: 't' },
    { k: 'boot', l: 'التشغيل عند بدء الجهاز (حيث يسمح النظام)', t: 't' }
];
function rows() {
    $('#srows').innerHTML = CFG.map(function (c) {
        if (c.g)
            return "<div class=\"rw\"><span class=\"gh\">".concat(c.g, "</span></div>");
        var ctl;
        if (c.t === 't')
            ctl = "<button class=\"btn ghost\" data-set=\"".concat(c.k, "\">").concat(S[c.k] ? 'مفعّل' : 'متوقف', "</button>");
        else if (c.t === 'n')
            ctl = "<button class=\"ib\" data-set=\"".concat(c.k, "\" data-d=\"-1\">\u2212</button><span>").concat(c.pct ? ar(Math.round(S[c.k] * 100)) + '٪' : ar(+(+S[c.k]).toFixed(2)), "</span><button class=\"ib\" data-set=\"").concat(c.k, "\" data-d=\"1\">+</button>");
        else
            ctl = "<button class=\"btn ghost\" data-set=\"".concat(c.k, "\">").concat((c.o.find(function (o) { return o[0] === S[c.k]; }) || c.o[0])[1], "</button>");
        return "<div class=\"rw\"><span>".concat(c.l, "</span><div class=\"c\">").concat(ctl, "</div></div>");
    }).join('');
}
function setOne(btn) {
    var k = btn.dataset.set, c = CFG.find(function (x) { return x.k === k; });
    if (!c)
        return;
    if (c.t === 't')
        S[k] = !S[k];
    else if (c.t === 'n')
        S[k] = Math.max(c.min, Math.min(c.max, +(S[k] + (+btn.dataset.d) * c.st).toFixed(2)));
    else {
        var i = c.o.findIndex(function (o) { return o[0] === S[k]; });
        S[k] = c.o[(i + 1) % c.o.length][0];
        if (k === 'city' && S.city === 'gps')
            askGps();
    }
    var keep = btn.dataset.d ? "[data-set=\"".concat(k, "\"][data-d=\"").concat(btn.dataset.d, "\"]") : "[data-set=\"".concat(k, "\"]");
    applySettings();
    rows();
    var n = $(keep, $('#srows'));
    if (n)
        n.focus();
}
function askGps() {
    if (!navigator.geolocation) {
        toast('الموقع غير متاح على هذا الجهاز');
        S.city = SAVED.city;
        rows();
        return;
    }
    navigator.geolocation.getCurrentPosition(function (p) { S.gps = { lat: p.coords.latitude, lon: p.coords.longitude }; toast('تم تحديد موقعك'); }, function () { toast('لم يُمنح إذن الموقع — اختر المدينة يدويًا'); S.city = SAVED.city === 'gps' ? 'madinah' : SAVED.city; S.gps = SAVED.gps; rows(); }, { timeout: 15000 });
}
function applySettings() {
    var c = S, b = document.body, r = document.documentElement.style;
    b.classList.toggle('nosecs', !c.secs);
    b.classList.toggle('nodate', !c.date);
    b.classList.toggle('nowx', !c.wx);
    b.classList.toggle('noprayers', !c.prayers);
    b.classList.toggle('nolist', !c.list);
    b.classList.toggle('listleft', !!c.listLeft);
    b.classList.toggle('fx', !!c.fx);
    b.classList.remove('acc-blue', 'acc-orange', 'acc-white');
    if (c.acc === 'blue' || c.acc === 'orange' || c.acc === 'white')
        b.classList.add('acc-' + c.acc);
    r.setProperty('--clock', c.clock);
    r.setProperty('--listw', c.listw + 'rem');
    r.setProperty('--dim', c.dim);
    dateFmts();
    _lastDay = '';
    pk = '';
    tickClock();
    renderWx();
    render();
    ui();
    if (PT)
        tickPrayers(true);
}
function commit() {
    var need = SAVED.city !== S.city || SAVED.method !== S.method || SAVED.asr !== S.asr || SAVED.unit !== S.unit || JSON.stringify(SAVED.gps) !== JSON.stringify(S.gps);
    var bootChanged = SAVED.boot !== S.boot;
    SAVED = Object.assign({}, S);
    store.set('settings', S);
    applySettings();
    if (bootChanged && window.Android && Android.setBoot) {
        try {
            Android.setBoot(!!S.boot);
        }
        catch (e) { }
    }
    if (need) {
        PT = null;
        WX = null;
        pk = '';
        refreshTimes();
        refreshWx();
    }
}
var mTab = 'tv', mEdit = -1, fType = 'yt', mcf = -1;
function renderManage() {
    var arr = L[mTab];
    $$('#mseg button').forEach(function (b) { return b.classList.toggle('on', b.dataset.m === mTab); });
    $('#mlist').innerHTML = arr.map(function (c, i) { return "<div class=\"mrow\"><button class=\"nmx f\" data-act=\"medit\" data-i=\"".concat(i, "\">").concat(esc(c.n), "<small class=\"").concat(c.u ? '' : 'warn', "\">").concat(c.u ? esc(c.c || '') : 'بلا رابط', "</small></button>\n    <button class=\"sm\" data-act=\"mfav\" data-i=\"").concat(i, "\" aria-label=\"\u0645\u0641\u0636\u0644\u0629\">").concat(c.f ? '★' : '☆', "</button>\n    <button class=\"sm\" data-act=\"mup\" data-i=\"").concat(i, "\" aria-label=\"\u0623\u0639\u0644\u0649\">\u25B2</button><button class=\"sm\" data-act=\"mdn\" data-i=\"").concat(i, "\" aria-label=\"\u0623\u0633\u0641\u0644\">\u25BC</button>\n    <button class=\"sm\" data-act=\"mdel\" data-i=\"").concat(i, "\">").concat(mcf === i ? 'تأكيد الحذف؟' : 'حذف', "</button></div>"); }).join('') || '<div class="empty">لا توجد عناصر</div>';
}
function openForm(i) {
    mEdit = i;
    var c = i < 0 ? { n: '', t: mTab === 'tv' ? 'hls' : 'audio', u: '', l: '', c: '', v: S.vol } : L[mTab][i];
    fType = mTab === 'radio' ? 'audio' : (c.t === 'yt' ? 'yt' : 'hls');
    $('#f-name').value = c.n;
    $('#f-url').value = c.u;
    $('#f-logo').value = c.l;
    $('#f-cat').value = c.c;
    $('#f-vol').value = c.v;
    $('#f-msg').textContent = 'للأفضل استخدم رابط بث مباشر ينتهي بـ m3u8. روابط يوتيوب قد لا تعمل داخل بعض المتصفحات.';
    typeLabel();
    $('#mform').style.display = 'grid';
    $('#f-name').focus();
    $('#mform').scrollIntoView({ block: 'nearest' });
}
function typeLabel() { $('#f-type').textContent = fType === 'yt' ? 'YouTube' : fType === 'hls' ? 'بث مباشر متوافق' : 'بث صوتي'; }
function saveForm() {
    var n = $('#f-name').value.trim(), u = $('#f-url').value.trim(), msg = $('#f-msg');
    if (!n) {
        msg.textContent = 'أدخل اسمًا.';
        $('#f-name').focus();
        return;
    }
    var e = validate(fType === 'audio' ? 'hls' : fType, u);
    if (e) {
        msg.textContent = e;
        $('#f-url').focus();
        return;
    }
    var o = { n: n, t: fType, u: u, l: $('#f-logo').value.trim(), c: $('#f-cat').value.trim(), v: Math.max(0, Math.min(100, +$('#f-vol').value || 80)), f: false };
    var idx;
    if (mEdit < 0) {
        L[mTab].push(o);
        idx = L[mTab].length - 1;
    }
    else {
        o.f = L[mTab][mEdit].f;
        L[mTab][mEdit] = o;
        idx = mEdit;
    }
    saveLists();
    $('#mform').style.display = 'none';
    closeSheet(true);
    openSheetId = null;
    setMode(mTab, true);
    render();
    toast('تم الحفظ');
    if (u)
        play(mTab, idx);
    else
        renderManage();
    var f = $("#list [data-i=\"".concat(idx, "\"]"));
    if (f)
        f.focus();
}
function openManage(m, i) { openSheet('manage'); mTab = m; renderManage(); if (i != null)
    openForm(i); }
function moveItem(i, d) {
    var _a;
    var a = L[mTab], j = i + d;
    if (j < 0 || j >= a.length)
        return;
    var curItem = L[P.mode][P.idx[P.mode]];
    _a = __read([a[j], a[i]], 2), a[i] = _a[0], a[j] = _a[1];
    saveLists();
    P.idx[P.mode] = Math.max(0, L[P.mode].indexOf(curItem));
    renderManage();
    render();
    var b = $("#mlist [data-act=\"".concat(d < 0 ? 'mup' : 'mdn', "\"][data-i=\"").concat(j, "\"]"));
    if (b)
        b.focus();
}
var endAt = 0;
function startTimer(m) { endAt = Date.now() + m * 60000; updateTimerLabel(); toast('بدأ المؤقت: ' + ar(m) + ' دقيقة'); }
function updateTimerLabel() {
    var el = $('#tleft');
    if (!endAt) {
        el.textContent = 'لا يوجد مؤقت';
        return;
    }
    var r = Math.max(0, endAt - Date.now());
    el.textContent = ar(p2(r / 3600e3 | 0)) + ':' + ar(p2(r % 3600e3 / 6e4 | 0)) + ':' + ar(p2(r % 6e4 / 1e3 | 0));
}
function sleepNow() { var it = L[P.mode] && L[P.mode][P.idx[P.mode]]; var n = $('#snow'); if (n)
    n.textContent = (P.kind && P.playing && it) ? '🔊 ' + it.n + ' — الصوت يعمل' : ''; }
function enterSleep() { closeSheet(true); sleepNow(); document.body.classList.add('sleeping'); $('#sleepscr').setAttribute('aria-hidden', 'false'); pk = ''; tickPrayers(true); renderWx(); }
function exitSleep() { document.body.classList.remove('sleeping'); $('#sleepscr').setAttribute('aria-hidden', 'true'); var f = $('#list .item.cur') || $('.ib'); f && f.focus(); }
function tickTimer() {
    if (!endAt)
        return;
    updateTimerLabel();
    if (Date.now() >= endAt) {
        endAt = 0;
        stopPlayback();
        updateTimerLabel();
    }
}
function setFull(on) {
    document.body.classList.toggle('full', on);
    var h = $('#fullhint');
    if (on) {
        h.classList.add('on');
        setTimeout(function () { return h.classList.remove('on'); }, 3500);
    }
    if (!on) {
        var f = $('#list .item.cur') || $('.ib');
        f && f.focus();
    }
    else {
        var b = $('#bplay');
        b.focus();
    }
    ui();
}
document.addEventListener('click', function (e) {
    if (document.body.classList.contains('sleeping')) {
        exitSleep();
        return;
    }
    var set = e.target.closest('[data-set]');
    if (set) {
        setOne(set);
        return;
    }
    var pill = e.target.closest('#wx');
    if (pill) {
        wxExp = !wxExp;
        renderWx();
        return;
    }
    if (wxExp) {
        wxExp = false;
        renderWx();
    }
    var b = e.target.closest('[data-act]');
    if (!b)
        return;
    var a = b.dataset.act, i = +b.dataset.i;
    switch (a) {
        case 'sheet':
            openSheet(b.dataset.s);
            break;
        case 'close':
            closeSheet();
            break;
        case 'full':
            setFull(!document.body.classList.contains('full'));
            break;
        case 'prev':
            step(-1);
            break;
        case 'next':
            step(1);
            break;
        case 'play':
            togglePlay();
            break;
        case 'mute':
            toggleMute();
            break;
        case 'vol':
            setVol(P.vol + (+b.dataset.d));
            break;
        case 'aud':
            document.body.classList.toggle('aud');
            ui();
            break;
        case 'mode': {
            var m = b.dataset.m;
            if (m !== P.mode) {
                stopPlayback();
                setMode(m);
                if (S.autoplay)
                    play(m, P.idx[m]);
            }
            break;
        }
        case 'favf':
            P.fav[P.mode] = !P.fav[P.mode];
            render();
            ui();
            break;
        case 'sel':
            if (i === P.idx[P.mode] && P.kind) {
                togglePlay();
            }
            else
                play(P.mode, i);
            break;
        case 's-save': {
            document.body.classList.remove('pv');
            commit();
            closeSheet();
            toast('تم حفظ الإعدادات');
            break;
        }
        case 's-cancel': {
            document.body.classList.remove('pv');
            S = Object.assign({}, SAVED);
            applySettings();
            closeSheet();
            break;
        }
        case 's-preview': {
            document.body.classList.add('pv');
            $('#sheet-settings').classList.remove('on');
            setTimeout(function () { return $('#pvbar button').focus(); }, 30);
            break;
        }
        case 's-reset':
            S = Object.assign({}, DEFAULTS);
            applySettings();
            rows();
            toast('استُعيدت القيم الافتراضية — اضغط حفظ للتثبيت');
            break;
        case 's-export': {
            var t = $('#io');
            t.style.display = 'block';
            t.value = JSON.stringify({ s: S, l: L });
            t.focus();
            t.select();
            break;
        }
        case 's-import': {
            var t = $('#io');
            if (t.style.display === 'none') {
                t.style.display = 'block';
                t.value = '';
                t.focus();
                toast('الصق الإعدادات ثم اضغط استيراد');
                break;
            }
            try {
                var o = JSON.parse(t.value);
                if (o.s)
                    S = Object.assign({}, DEFAULTS, o.s);
                if (o.l && Array.isArray(o.l.tv) && Array.isArray(o.l.radio)) {
                    L = o.l;
                    saveLists();
                    render();
                }
                SAVED = Object.assign({}, SAVED, { city: '' });
                commit();
                rows();
                toast('تم الاستيراد');
            }
            catch (x) {
                toast('نص الإعدادات غير صالح');
            }
            break;
        }
        case 'mtab':
            mTab = b.dataset.m;
            $('#mform').style.display = 'none';
            mcf = -1;
            renderManage();
            break;
        case 'madd':
            openForm(-1);
            break;
        case 'medit':
            openForm(i);
            break;
        case 'mfav':
            L[mTab][i].f = !L[mTab][i].f;
            saveLists();
            renderManage();
            render();
            break;
        case 'mup':
            moveItem(i, -1);
            break;
        case 'mdn':
            moveItem(i, 1);
            break;
        case 'mdel': {
            if (mcf !== i) {
                mcf = i;
                renderManage();
                var n = $("#mlist [data-act=\"mdel\"][data-i=\"".concat(i, "\"]"));
                n && n.focus();
                break;
            }
            mcf = -1;
            var wasCur = mTab === P.mode && i === P.idx[P.mode];
            L[mTab].splice(i, 1);
            saveLists();
            if (mTab === P.mode) {
                if (i < P.idx[P.mode])
                    P.idx[P.mode]--;
                P.idx[P.mode] = Math.max(0, Math.min(P.idx[P.mode], L[P.mode].length - 1));
                if (wasCur)
                    stopPlayback();
            }
            renderManage();
            render();
            toast('تم الحذف');
            break;
        }
        case 'ftype':
            if (mTab === 'tv') {
                fType = fType === 'yt' ? 'hls' : 'yt';
                typeLabel();
            }
            break;
        case 'fsave':
            saveForm();
            break;
        case 'fcancel':
            $('#mform').style.display = 'none';
            break;
        case 'timer':
            startTimer(+b.dataset.m);
            break;
        case 'timerc': {
            var m = +$('#cm').value;
            if (!(m > 0)) {
                toast('أدخل عدد دقائق صحيحًا');
                break;
            }
            startTimer(m);
            break;
        }
        case 'timerx':
            endAt = 0;
            updateTimerLabel();
            toast('أُلغي المؤقت');
            break;
        case 'sleepnow':
            enterSleep();
            break;
    }
});
var hotT = 0;
function hot() { var p = $('#player'); p.classList.add('hot'); clearTimeout(hotT); hotT = setTimeout(function () { return p.classList.remove('hot'); }, 4000); }
function containerEls() {
    var root = openSheetId ? $('#sheet-' + openSheetId) : document.body.classList.contains('pv') ? $('#pvbar') : $('#app');
    return $$('button,input,textarea', root).filter(function (e) { return e.offsetParent !== null && !e.disabled && e.tabIndex >= 0 && getComputedStyle(e).visibility !== 'hidden'; });
}
function move(dir) {
    var e_6, _a;
    var a = document.activeElement, els = containerEls();
    if (!els.length)
        return;
    if (!els.includes(a)) {
        (els.find(function (e) { return e.classList.contains('cur'); }) || els[0]).focus();
        return;
    }
    var r = a.getBoundingClientRect(), cx = r.left + r.width / 2, cy = r.top + r.height / 2;
    var best = null, bs = 1e12;
    try {
        for (var els_1 = __values(els), els_1_1 = els_1.next(); !els_1_1.done; els_1_1 = els_1.next()) {
            var e = els_1_1.value;
            if (e === a)
                continue;
            var q = e.getBoundingClientRect(), x = q.left + q.width / 2 - cx, y = q.top + q.height / 2 - cy;
            var ok = dir === 'L' ? x < -4 : dir === 'R' ? x > 4 : dir === 'U' ? y < -4 : y > 4;
            if (!ok)
                continue;
            var horiz = dir === 'L' || dir === 'R', pr = Math.abs(horiz ? x : y), cr = Math.abs(horiz ? y : x), s = pr + cr * 2.6;
            if (s < bs) {
                bs = s;
                best = e;
            }
        }
    }
    catch (e_6_1) { e_6 = { error: e_6_1 }; }
    finally {
        try {
            if (els_1_1 && !els_1_1.done && (_a = els_1.return)) _a.call(els_1);
        }
        finally { if (e_6) throw e_6.error; }
    }
    if (best) {
        best.focus();
        if (best.closest('.list,.mlist,.panel'))
            best.scrollIntoView({ block: 'nearest', inline: 'nearest' });
    }
}
var exitArmed = 0;
window.omarsBack = function () {
    var b = document.body;
    if (b.classList.contains('sleeping')) {
        exitSleep();
        return true;
    }
    if (b.classList.contains('pv')) {
        $('[data-act="s-cancel"]').click();
        return true;
    }
    if (openSheetId) {
        if ($('#mform').style.display !== 'none' && openSheetId === 'manage') {
            $('#mform').style.display = 'none';
            return true;
        }
        if (openSheetId === 'settings')
            $('[data-act="s-cancel"]').click();
        else
            closeSheet();
        return true;
    }
    if (b.classList.contains('full')) {
        setFull(false);
        return true;
    }
    if (Date.now() - exitArmed < 2500)
        return false;
    exitArmed = Date.now();
    toast('اضغط Back مرة أخرى للخروج');
    return true;
};
window.omarsKey = function (k) {
    if (k === 'playpause')
        togglePlay();
    else if (k === 'stop')
        stopAll();
    else if (k === 'next')
        step(1);
    else if (k === 'prev')
        step(-1);
    hot();
};
document.addEventListener('keydown', function (e) {
    var k = e.key, b = document.body, typing = /^(INPUT|TEXTAREA)$/.test(document.activeElement.tagName);
    if (b.classList.contains('sleeping')) {
        if (k === 'ArrowUp' || k === 'ArrowDown') {
            S.dim = SAVED.dim = Math.max(.05, Math.min(1, +(S.dim + (k === 'ArrowUp' ? .05 : -.05)).toFixed(2)));
            document.documentElement.style.setProperty('--dim', S.dim);
            store.set('settings', S);
        }
        else if (k === 'ArrowLeft' || k === 'ArrowRight') {
            setVol(P.vol + (k === 'ArrowRight' ? 10 : -10));
        }
        else if (k === 'Enter' || k === 'Escape' || k === 'Backspace' || k === 'GoBack')
            exitSleep();
        e.preventDefault();
        return;
    }
    if (k === 'MediaPlayPause' || k === 'MediaPlay' || k === 'MediaPause') {
        e.preventDefault();
        togglePlay();
        hot();
        return;
    }
    if (k === 'MediaStop') {
        e.preventDefault();
        stopAll();
        return;
    }
    if (k === 'ChannelUp' || k === 'PageUp') {
        e.preventDefault();
        step(-1);
        return;
    }
    if (k === 'ChannelDown' || k === 'PageDown') {
        e.preventDefault();
        step(1);
        return;
    }
    if (k === 'Escape' || k === 'GoBack' || k === 'BrowserBack' || (k === 'Backspace' && !typing)) {
        e.preventDefault();
        window.omarsBack();
        return;
    }
    if (b.classList.contains('full') && !openSheetId) {
        if (k === 'ArrowUp') {
            e.preventDefault();
            step(-1);
            toast((cur() || {}).n || '', 1800);
            return;
        }
        if (k === 'ArrowDown') {
            e.preventDefault();
            step(1);
            toast((cur() || {}).n || '', 1800);
            return;
        }
        if (k === 'ArrowLeft' || k === 'ArrowRight') {
            e.preventDefault();
            setVol(P.vol + (k === 'ArrowRight' ? 5 : -5));
            toast('الصوت ' + ar(P.vol) + '٪', 1200);
            return;
        }
        if (k === 'Enter') {
            e.preventDefault();
            togglePlay();
            return;
        }
    }
    var m = { ArrowLeft: 'L', ArrowRight: 'R', ArrowUp: 'U', ArrowDown: 'D' }[k];
    if (m) {
        if (typing && (m === 'L' || m === 'R'))
            return;
        e.preventDefault();
        move(m);
        hot();
        return;
    }
    if (k === 'Enter' && document.activeElement && document.activeElement.id === 'wx') {
        e.preventDefault();
        document.activeElement.click();
    }
});
window.addEventListener('scroll', function () { return window.scrollTo(0, 0); });
try {
    for (var _b = __values(['app', 'shift']), _c = _b.next(); !_c.done; _c = _b.next()) {
        var id = _c.value;
        document.getElementById(id).addEventListener('scroll', function (e) { e.target.scrollLeft = 0; e.target.scrollTop = 0; });
    }
}
catch (e_1_1) { e_1 = { error: e_1_1 }; }
finally {
    try {
        if (_c && !_c.done && (_a = _b.return)) _a.call(_b);
    }
    finally { if (e_1) throw e_1.error; }
}
function shiftTick() {
    var r = document.documentElement.style, sleeping = document.body.classList.contains('sleeping');
    if ((!sleeping && !S.burn) || (sleeping && !S.sleepFx)) {
        r.setProperty('--sx', '0px');
        r.setProperty('--sy', '0px');
        return;
    }
    r.setProperty('--sx', (Math.random() * 20 - 10 | 0) + 'px');
    r.setProperty('--sy', (Math.random() * 12 - 6 | 0) + 'px');
}
function init() {
    applySettings();
    var lastSel = store.get('last', null);
    if (lastSel && L[lastSel.m] && L[lastSel.m][lastSel.i]) {
        P.mode = lastSel.m;
        P.idx[lastSel.m] = lastSel.i;
    }
    setMode(P.mode, true);
    render();
    ui();
    tickClock();
    setInterval(tickClock, 500);
    setInterval(function () { tickPrayers(); tickTimer(); }, 1000);
    setInterval(shiftTick, 60000);
    refreshTimes();
    refreshWx();
    setInterval(refreshWx, 20 * 60000);
    window.addEventListener('online', function () { refreshTimes(); refreshWx(); if (!P.kind && S.autoplay)
        autoStart(); });
    requestAnimationFrame(drawViz);
    requestAnimationFrame(drawFx);
    var rm = matchMedia('(prefers-reduced-motion:reduce)').matches;
    setTimeout(function () { $('#splash').classList.add('gone'); autoStart(); }, rm ? 100 : 1700);
    setTimeout(function () { var s = $('#splash'); s && s.remove(); }, rm ? 600 : 3200);
}
function autoStart() {
    if (!S.autoplay) {
        var f = $('#list .item.cur') || $('.ib');
        f && f.focus();
        return;
    }
    var m = P.mode, arr = L[m];
    var i = P.idx[m];
    if (!arr[i] || !arr[i].u) {
        var j = arr.findIndex(function (c) { return c.u; });
        if (j >= 0)
            i = j;
    }
    if (arr.length) {
        play(m, i);
        P.fb = 4;
    }
    setTimeout(function () { var f = $('#list .item.cur') || $('.ib'); if (f && document.activeElement === document.body)
        f.focus(); }, 100);
}
init();
window.__appOk = true;
