<html>
<body>
<div>
    <script type="text/javascript" SRC="${request.contextPath}/marvin/marvin.js"></script>
    <script type="text/javascript">
        msketch_name = "MarvinSketch";
        msketch_begin("${request.contextPath}/marvin", 540, 480);
        msketch_end();
    </script>
</div>

<div>
    <table class="skinnyTable" style="margin-top: 10px; float: right;" align="left">
        <tr>
            <td style="padding-right: 20px;">
                <g:radioGroup name="structureSearchMatchType" values="['exact', 'substructure', 'similarity']"
                              value="substructure"
                              labels="['exact match', 'substructure', 'similarity']">
                    <p>${it.radio} ${it.label}</p>
                </g:radioGroup>
            </td>
            <td>
                <g:submitButton name="search" value="Search"/>
                <g:submitButton name="cancel" value="Cancel"/>
            </td>
        </tr>
    </table>
</div>
</body>
</html>