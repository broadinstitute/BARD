						<div class="card">
                            <table class="gridtable">
                                <caption>${card.title}</caption>
                                <tbody>
                                <g:each in="${card.lines}" status="i" var="line">
                                    <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                                        <td>${line.attributeLabel}</td>
                                        <td>${line.valueLabel}</td>
                                    </tr>
                                </g:each>
                                </tbody>
                            </table>
                        </div>