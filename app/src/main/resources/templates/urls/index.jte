@import hexlet.code.util.NamedRoutes
@import hexlet.code.dto.urls.UrlsPage
@import hexlet.code.util.Util
@param UrlsPage page

@template.layout.page(
    page = page,
    content = @`
        <div class="container-lg mt-5">
            <h1>Сайты</h1>

            <table class="table table-bordered table-hover mt-3">
                <thead>
                <tr>
                    <th class="col-1">ID</th>
                    <th>Имя</th>
                    <th class="col-2">Последняя проверка</th>
                    <th class="col-1">Код ответа</th>
                </tr>
                </thead>

                <tbody>
                @if(!page.getUrls().isEmpty())
                    @for(var url : page.getUrls())
                        <tr>
                            <td>
                                ${url.getId()}
                            </td>
                            <td>
                                <a href="${NamedRoutes.urlPath(url.getId())}">${url.getName()}</a>
                            </td>
                            <td>
                                @if(page.getLatestUrlsChecks().get(url.getId()) != null)
                                    ${Util.getParsedCreatedAt(page.getLatestUrlsChecks().get(url.getId()).getCreatedAt())}
                                @endif
                            </td>
                            <td>
                                @if(page.getLatestUrlsChecks().get(url.getId()) != null)
                                    ${page.getLatestUrlsChecks().get(url.getId()).getStatusCode()}
                                @endif
                            </td>
                        </tr>
                    @endfor
                @endif
                </tbody>
            </table>
        </div>
    `
)
