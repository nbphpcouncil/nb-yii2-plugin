nb-yii2-plugin
=============

Yii2 Framework plugin for NetBeans 8.0+

## Requirements

- NetBeans 8.0+ (depend on build version of NetBeans now)
- Yii2 Framework (app-basic)
- Composer

## Features

- Badge icon
- New Project Wizard (create a project via composer)
- Go to View/Action Action
- Create htaccess Action
- Hyperlink Navigation
- Custom alias paths support

## How to enable

Right-click your php project > Frameworks > Yii2 > Check `Enabled`

## Project settings

### Base path of application

Have a look at http://www.yiiframework.com/doc-2.0/guide-structure-applications.html#basepath

### Path aliases file path

Path aliases file provides support for your custom directory structure.
These aliases will be used when navigation feature (e.g. Go To Action) is used.
It's json file. It has a format like the following:

```javascript
{
	"aliases": {
		"@app/controllers" : ["controllers", "mycontrollers"],
		"@app/models" : ["mymodels"],
		"@app/views" : ["themes/basic", "views", "views2"],
		"@yii": ["vendor/yiisoft/yii2"]
	}
}
```

**NOTE**: Can't recognize altogether aliases.

## Downloads

- https://github.com/nbphpcouncil/nb-yii2-plugin/releases

## License
[Common Development and Distribution License (CDDL) v1.0 and GNU General Public License (GPL) v2](http://netbeans.org/cddl-gplv2.html)
