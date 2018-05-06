
var version = 3;
var offlinePage = '/offline-page.html';

var CACHE_NAME = 'diplom-cache-v'+version;
var urlsToCache = [
    offlinePage
];

self.addEventListener('install', function(event) {
    // установка
    console.log("УСТАНОВКА СЕРВИСНГО РАБОТАНИКА")
    event.waitUntil(
        caches.open(CACHE_NAME)
            .then(function(cache) {
                return cache.addAll(urlsToCache);
            })
    );
});

self.addEventListener('fetch', function(event) {

    var request = event.request;
    if (doesRequestAcceptHtml(request)) {
        // HTML pages fallback to offline page
        event.respondWith(
            fetch(request)
                .catch(function() {
                    return caches.match(offlinePage);
                })
        );
    } else {
        if (request.cache === 'only-if-cached' && request.mode !== 'same-origin') {
            return;
        }
        event.respondWith(
            caches.match(request)
                .then(function(response) {
                    return response || fetch(request);
                })
        );
    }
});

var doesRequestAcceptHtml = function(request) {
    return request.headers.get('Accept')
        .split(',')
        .some(function(type) {
            return type === 'text/html';
        });
};
