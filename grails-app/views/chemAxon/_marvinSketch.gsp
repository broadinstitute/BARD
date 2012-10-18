<%-- Sets up the MarvinSketch applet. Additional MarvinSketch param could be set here --%>
<script type="text/javascript" SRC="${request.contextPath}/marvin/marvin.js"></script>
<script type="text/javascript">
    msketch_name = "MarvinSketch";
    msketch_begin("${request.contextPath}/marvin", window.innerWidth / 2, window.innerHeight / 2);
    msketch_end();
</script>
