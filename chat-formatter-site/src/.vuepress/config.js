const { description } = require('../../package')

module.exports = {

  title: 'ChatFormatter',
  description: description,

  head: [
    ['meta', { name: 'theme-color', content: '#3eaf7c' }],
    ['meta', { name: 'apple-mobile-web-app-capable', content: 'yes' }],
    ['meta', { name: 'apple-mobile-web-app-status-bar-style', content: 'black' }],
      ['meta', { name: 'apple-mobile-web-app-capable', content: 'yes' }]
  ],

  themeConfig: {
    repo: '',
    editLinks: true,
    docsDir: 'chat-formatter-site',
    editLinkText: '',
    smoothScroll: true,
    lastUpdated: true,
    nav: [
      {
        text: 'Guide',
        link: '/guide/',
      },
      {
        text: 'GitHub',
        link: 'https://github.com/EternalCodeTeam/ChatFormatter'
      }
    ],
    sidebar: {
      '/guide/': [
        {
          title: 'Guide',
          collapsable: true,
          children: [
            '',
          ]
        }
      ],
    }
  },
    
  plugins: [
      ['@vuepress/back-to-top', true],
      [
          '@vuepress/pwa',
          {
              serviceWorker: true,
              updatePopup: true
          }
      ],
      ['@vuepress/medium-zoom', true],
      ['vuepress-plugin-flowchart']
  ]
}
