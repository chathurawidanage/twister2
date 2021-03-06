//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
import React from "react";
import {Button, Tooltip, Position} from "@blueprintjs/core";

export default class NodeTag extends React.Component {

    render() {
        return (
            <Tooltip content="Node" position={Position.TOP}>
                <Button
                    text={this.props.node.cluster.name + "/" +
                    this.props.node.dataCenter + "/"
                    + this.props.node.rack + "/"
                    + this.props.node.ip}
                    minimal={true}
                    icon={"desktop"} small={true}/>
            </Tooltip>
        );
    }
}