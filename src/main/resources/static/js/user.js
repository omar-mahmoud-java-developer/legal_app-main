document.addEventListener('scroll', function () {
    if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight) {
        document.body.classList.add('scroll-footer');
    } else {
        document.body.classList.remove('scroll-footer');
    }
});
