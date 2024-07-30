package org.kiteio.punica.candy

/**
 * URL 合辑
 */
object URLs {
    /** Github */
    private const val GITHUB = "https://github.com"

    /** Gitee */
    const val GITEE = "https://gitee.com"

    /** Punica 仓库路由 */
    const val PUNICA_ROUTE = "Kiteio/Punica"

    /** Punica Github */
    const val PUNICA_GITHUB = "$GITHUB/$PUNICA_ROUTE"

    /** Punica Gitee */
    const val PUNICA_GITEE = "$GITEE/$PUNICA_ROUTE"

    /** Tessdata 训练文件 */
    const val TESSDATA = "$GITEE/$PUNICA_ROUTE/raw/master/tessdata/eng.traineddata"
}